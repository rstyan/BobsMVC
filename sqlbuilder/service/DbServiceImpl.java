package com.clickability.cms.dataaccess.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.clickability.cms.dataaccess.sqlbuilder.Binding;
import com.clickability.cms.dataaccess.sqlbuilder.UpdateAssignment;
import com.clickability.cms.dataaccess.sqlbuilder.Conjunction;
import com.clickability.cms.dataaccess.sqlbuilder.DataaccessException;
import com.clickability.cms.dataaccess.sqlbuilder.Delete;
import com.clickability.cms.dataaccess.sqlbuilder.Insert;
import com.clickability.cms.dataaccess.sqlbuilder.Equal;
import com.clickability.cms.dataaccess.sqlbuilder.Expression;
import com.clickability.cms.dataaccess.sqlbuilder.Query;
import com.clickability.cms.dataaccess.sqlbuilder.QueryManager;
import com.clickability.cms.dataaccess.sqlbuilder.Resource;
import com.clickability.cms.dataaccess.sqlbuilder.UpdateManager;
import com.clickability.cms.dataaccess.sqlbuilder.TransactionManagerFactory;
import com.clickability.cms.dataaccess.sqlbuilder.Update;
import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbField;
import com.clickability.cms.dataaccess.sqlbuilder.annotations.Reference;
import com.clickability.cms.dataaccess.sqlbuilder.annotations.Table;
import com.clickability.dbmanager.DBManager;

/**
 * A generic service class for basic CRUD operations.
 * 
 * @author roystyan
 *
 */
public abstract class DbServiceImpl<T> implements DbService<T> {
	
	private static Logger logger = Logger.getLogger(DbServiceImpl.class);
	
	protected final Class<T> classT;
	protected final Table tableDescriptor;
	
	protected DbServiceImpl(Class<T> classT) {
		// TODO  This sucks.  Normally the ParameterizedType technique would work, but not when we try to bind an intercepter
		// to handle @Transactional.  So subclasses will have to tell us what the paramaterized type is.
		// can we get around this?
		// this.classT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	    this.classT = classT;
	    this.tableDescriptor = this.getTable();
	}
	
	@Override
	public T findById(Map<String, Object> primaryKey) throws Exception {
		Query query = new Query().from(getResource()).where(getPrimaryKeyComparator(primaryKey.keySet()));
		return fetch(query, getKeyBindings(primaryKey));
	}
	
	protected List<T> fetchAll(Query query, Binding...bindings) throws Exception {
		try (QueryManager qm = new QueryManager(this.getDatabaseManager())) {
			return qm.execute(query, bindings, rs->loadResults(rs, this.classT));
		}		
	}
	
	protected List<T> fetchAll(Query query, List<Binding> bindings) throws Exception {
		try (QueryManager qm = new QueryManager(this.getDatabaseManager())) {
			return qm.execute(query, bindings, rs->loadResults(rs, this.classT));
		}		
	}
	
	protected List<T> fetchAll(Query query) throws Exception {
		List<Binding> bindings = new ArrayList<>();
		return fetchAll(query, bindings);
	}

	protected <R> List<R> fetchAll(Query query, Binding[] bindings, Function<ResultSet, R> loader) throws SQLException {
		try (QueryManager qm = new QueryManager(this.getDatabaseManager())) {
			return qm.execute(query, bindings, loader);
		}
	}

	protected <R> List<R> fetchAll(Query query, List<Binding> bindings, Function<ResultSet, R> loader) throws SQLException {
		try (QueryManager qm = new QueryManager(this.getDatabaseManager())) {
			return qm.execute(query, bindings, loader);
		}
	}

	protected T fetch(Query query, Binding...bindings) throws Exception {
		try (QueryManager qm = new QueryManager(this.getDatabaseManager())) {
			List<T> results = qm.execute(query, bindings, rs->loadResults(rs, this.classT));
			return results.size() == 1 ? results.get(0) : null;
		}		
	}
	
	protected T fetch(Query query, List<Binding> bindings) throws Exception {
		return fetch(query, bindings, this.classT);
	}
	
	protected <R> R fetch(Query query, List<Binding> bindings, Class<R> classR) throws Exception {
		try (QueryManager qm = new QueryManager(this.getDatabaseManager())) {
			List<R> results = qm.execute(query, bindings, rs->loadResults(rs, classR));
			return results.size() == 1 ? results.get(0) : null;
		}		
	}
	
	protected T fetch(Query query) throws Exception {
		List<Binding> bindings = new ArrayList<Binding>();
		return fetch(query, bindings);
	}
	
	protected <R> R loadResults(ResultSet rs, Class<R> classR) {
		R object = newInstance(classR);
		for (Field field : getAllDbFields(classR)) {
			DbField annotation = field.getAnnotation(DbField.class);
			if (annotation != null) {
				try {
					Object value = rs.getObject(annotation.value());
					setValue(object, value, field, annotation.mapsto());
				}
				catch (Exception e) {
					logger.error(String.format("exception on field %s", annotation.value()));
					throw new DataaccessException(e);
				}
			}
		}
		for (Field field : this.getAllReferences(classR)) {
			Reference annotation = field.getAnnotation(Reference.class);
			if (annotation != null) {
				try {
					Object primaryValue = rs.getObject(annotation.foreignKey());
					if (primaryValue != null) {
						Map<String, Object> primaryKey = new HashMap<>();
						primaryKey.put(annotation.referencedField(), primaryValue);
						Class<?> referencedClass = field.getType();
						Resource referencedTable = getResource(referencedClass);
						if (referencedTable != null) {
							Expression pkComparator = getPrimaryKeyComparator(primaryKey.keySet());
							Query query = new Query().from(referencedTable).where(pkComparator);
							List<Binding> bindings = getKeyBindings(primaryKey);
							Object value = fetch(query, bindings, referencedClass);
							setValue(object, value, field, void.class);
						}
					}
				}
				catch (Exception e) {
					logger.error(String.format("exception on reference %s", annotation.foreignKey()));
					throw new DataaccessException(e);
				}
			}
		}
		return object;
	}
	
	private <R> void setValue(R object, Object value, Field field, Class<?> parameterType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setter = null;
		if (!parameterType.equals(void.class)) {
			setter = getSetter(object.getClass(), field, parameterType);
		}
		if (setter == null) {
			parameterType = field.getType();
			setter = getSetter(object.getClass(), field, parameterType);
		}
		value = Binding.fromSqlType(value, field.getType());
		final boolean unsafeSet =  isSetValueUnsafe(value, field);

		if (!unsafeSet) {
			if (setter != null) {
				setter.setAccessible(true);
				setter.invoke(object, value);
			}
			else {
				field.setAccessible(true);
				field.set(object, value);
			}
		}
		else {
			logger.debug("Can't set field '" + field.getName() + "' to: " + value != null ? value : "null");
		}
	}

	/**
	 * Checks if the specified {@code dbValue} is safe to be set on the {@code javaField}.
	 * Currently only handles one edge case: {@code nulls} cannot be set on primivites. But it's
	 * possible that we might need to extend this to handle more edge cases.
	 *
	 * @param dbValue the value retrieved from the db that is to be set on the corresponding java field.
	 * @param javaField the java field on which the value retrieved from the db is to be set
	 * @return {@code true} if it is safe to set the value on the field or {@code false} otherwise.
	 */
	private static boolean isSetValueUnsafe(final Object dbValue, final Field javaField) {
		return dbValue == null && javaField.getType().isPrimitive();
	}

	private Method getSetter(Class<?> objectClass, Field field, Class<?> parameterType) {
		String[] prefixes = {"to", "set"};
		String fieldName = StringUtils.capitalize(field.getName());
		for (String prefix : prefixes) {
			Method setter = null;
			try {
				setter = objectClass.getDeclaredMethod(prefix+fieldName, parameterType);
				if (setter.getReturnType().equals(void.class)) {
					return setter;
				}
			} 
			catch (Exception e) {
				// no such method.  not a problem.
			}
		}
		// look no further than the class containing the declaring field.
		if (objectClass.equals(field.getDeclaringClass())) {
			return null;
		}
		Class<?> superClass = objectClass.getSuperclass();
		if (superClass != null) {
			return getSetter(superClass, field, parameterType);
		}
		return null;
	}
	
	/*
	 * Create a new Instance of a dataaccess class.  This method works by reflection and
	 * requires that the class declare an empty constructor. So the typical error is the 
	 * class does not, in fact, declare one.
	 * 
	 * Check the above first before debugging further.
	 * 
	 * TODO
	 * check out http://objenesis.org/tutorial.html for a way to
	 * instantiate objects without constructors.  Evil or Genius?
	 * For data access objects that are essentially just beans, this could be the
	 * way to go.
	 */
	private <R> R newInstance(Class<R> classR) {
		@SuppressWarnings("unchecked")
		Constructor<R>[] ctors = (Constructor<R>[]) classR.getDeclaredConstructors();
		Constructor<R> ctor = null;
		for (int i = 0; i < ctors.length; i++) {
		    ctor = ctors[i];
		    if (ctor.getGenericParameterTypes().length == 0) break;
		}
		
		if (ctor == null) {
			throw new DataaccessException("Missing empty constructor");
		}

		ctor.setAccessible(true);
		try {
			return ctor.newInstance();
		} 
		catch (Exception e) {
			throw new DataaccessException(e);
		} 
	}
	
	@Override
	public void delete(T object) throws IllegalArgumentException, IllegalAccessException, Exception {
		delete(getPrimaryKey(object));
	}
	
	protected void delete(Map<String,Object> primaryKey) throws Exception {
		Delete delExpr = new Delete(new Resource(tableDescriptor.value())).where(this.getPrimaryKeyComparator(primaryKey.keySet()));
		try (UpdateManager um = TransactionManagerFactory.get(tableDescriptor.database())) {
			List<Binding> bindings = getKeyBindings(primaryKey);
			um.execute(delExpr, bindings.toArray(new Binding[bindings.size()]));
		}		
	}

	@Override
	public T insert(T instance) throws Exception {
		Insert insert = new Insert(new Resource(tableDescriptor.value()));
		for (Field field : getAllDbFields(instance.getClass())) {
			DbField annotation = field.getAnnotation(DbField.class);
			if (!annotation.autogen()) {
				field.setAccessible(true);
				insert.add(new UpdateAssignment(annotation.value(), getBinding(instance, field, annotation)));
			}
		}

		for (Field field : getAllReferences(instance.getClass())) {
			field.setAccessible(true);
			Reference annotation = field.getAnnotation(Reference.class);

			// e.g. if the field is "customer" then this statement would be equivalent to:
			// Customer referencedObject = instance.getCustomer();
			Object referencedObject = field.get(instance);

			if (referencedObject != null) {
				UpdateAssignment value = getReferencedValue(annotation.foreignKey(), annotation.referencedField(), referencedObject);
				if (value != null) {
					insert.add(value);
				}
			}
		}

		try (UpdateManager qm = TransactionManagerFactory.get(tableDescriptor.database())) {
			Map<String,Field> primaryKey = getPrimaryKey();
			boolean isAutogen = isAutogen(primaryKey, instance);
			int newId = qm.execute(insert, isAutogen);
			if (isAutogen) {
				for (String fieldName : primaryKey.keySet()) {
					Field field = primaryKey.get(fieldName);
					field.setAccessible(true);
					field.set(instance, newId);
				}
			}
		}
		return instance;
	}
	
	private boolean isAutogen(Map<String,Field> pk, T instance) throws IllegalArgumentException, IllegalAccessException {
		// there can only be one.
		if (pk.size() == 1) {
			for (String name : pk.keySet()) {
				Field field = pk.get(name);
				DbField annotation = field.getAnnotation(DbField.class);
				field.setAccessible(true);
				Object fieldValue = field.get(instance);
				return annotation.autogen() && isUnassigned(fieldValue); 
			}
		}
		return false;
	}
	
	private boolean isUnassigned(Object value) {
		return value==null || (value instanceof Integer && (Integer) value < 1) || (value instanceof Long && (Long) value < 1);
	}
	
	@Override
	public void update(T object) throws Exception {
		Map<String,Field> primaryKey = getPrimaryKey();
		Update update = new Update(new Resource(tableDescriptor.value())).where(this.getPrimaryKeyComparator(primaryKey.keySet()));
		for (Field field : getAllDbFields(object.getClass())) {
			DbField annotation = field.getAnnotation(DbField.class);
			if (!annotation.autogen() && !annotation.primary() && annotation.mutable()) {
				field.setAccessible(true);
				update.add(new UpdateAssignment(annotation.value(), getBinding(object, field, annotation)));
			}
		}

		try (UpdateManager qm = TransactionManagerFactory.get(tableDescriptor.database())) {
			List<Binding> bindings = getKeyBindings(primaryKey, object);
			qm.execute(update, bindings.toArray(new Binding[bindings.size()]));
		}		
	}
	
	private Binding getBinding(T object, Field field, DbField descriptor) throws Exception {
		Method getter = null;
		Object value = null;
		Class<?> parameterType = descriptor.mapsto();
		// void means mapsto was not set, so ignore it.
		if (!parameterType.equals(void.class)) {
			getter = getGetter(object.getClass(), field, parameterType);
		};
		if (getter == null) {
			parameterType = field.getType();
			getter = getGetter(object.getClass(), field, parameterType);
		}
		if (getter != null) {
			getter.setAccessible(true);
			try {
				value = getter.invoke(object);
			}
			catch (InvocationTargetException e) {
				logger.error("Error invoking Getter", e.getTargetException());
				throw e;
			}
		}
		else {
			field.setAccessible(true);
			value = field.get(object);
		}
		return value == null ? new Binding(parameterType) : new Binding(value);
	}
	
	private Method getGetter(Class<?> objectClass, Field field, Class<?> parameterType) {
		String[] prefixes = {"from", "get"};
		String fieldName = StringUtils.capitalize(field.getName());
		for (String prefix : prefixes) {
			Method getter = null;
				try {
					getter = objectClass.getDeclaredMethod(prefix+fieldName);
					if (getter.getReturnType().equals(parameterType)) {
						return getter;
					}
				} 
				catch (Exception e) {
					// ignore
				} 
		}
		// look no further than the class containing the declaring field.
		if (objectClass.equals(field.getDeclaringClass())) {
			return null;
		}
		Class<?> superClass = objectClass.getSuperclass();
		if (superClass != null) {
			return getGetter(superClass, field, parameterType);
		}
		return null;
	}
	
	protected Table getTable() {
		return getTable(this.classT);
	}
	
	// Assumes table is declared on the current class
	// i.e.  not a superclass.  Should this constraint be relaxed?
	private Table getTable(Class<?> dbServiceClass) {
		Table tableAnnotation = dbServiceClass.getAnnotation(Table.class);
		if (tableAnnotation == null)  {
			throw new DataaccessException("Missing @Table annotation for class "+dbServiceClass.getSimpleName());
		}
		return tableAnnotation;
	}
	
	protected DBManager getDatabaseManager() {
		return DBManager.getDBManager(tableDescriptor.database());
	}
	
	// include inherited fields.
	protected List<Field> getAllDbFields(Class<?> clazz) {
		List<Field> dbFields = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			DbField fieldAnnotation = field.getAnnotation(DbField.class);
			if (fieldAnnotation != null) {
				dbFields.add(field);
			}
		}
		Class<?> parentClass = clazz.getSuperclass();
		if (parentClass != null) {
			dbFields.addAll(getAllDbFields(parentClass));
		}
		return dbFields;
	}

	// include inherited references.
	protected List<Field> getAllReferences(Class<?> clazz) {
		List<Field> references = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			Reference refAnnotation = field.getAnnotation(Reference.class);
			if (refAnnotation != null) {
				references.add(field);
			}
		}
		Class<?> parentClass = clazz.getSuperclass();
		if (parentClass != null) {
			references.addAll(getAllReferences(parentClass));
		}
		return references;
	}

	protected Map<String, Field> getPrimaryKey() {
		Map<String, Field> primaryKey = new HashMap<>();
		for (Field field : getAllDbFields(this.classT)) {
			DbField fieldAnnotation = field.getAnnotation(DbField.class);
			if (fieldAnnotation != null && fieldAnnotation.primary()) {
				field.setAccessible(true);
				primaryKey.put(fieldAnnotation.value(), field);
			}
		}
		if (primaryKey.size() < 1) {
			throw new DataaccessException("Missing primary key annotation");
		}
		return primaryKey;
	}

	protected Map<String, Object> getPrimaryKey(T object) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> primaryKey = new HashMap<>();
		for (Field field : getAllDbFields(this.classT)) {
			DbField fieldAnnotation = field.getAnnotation(DbField.class);
			if (fieldAnnotation != null && fieldAnnotation.primary()) {
				field.setAccessible(true);
				primaryKey.put(fieldAnnotation.value(), field.get(object));
			}
		}
		if (primaryKey.size() < 1) {
			throw new DataaccessException("Missing primary key annotation");
		}
		return primaryKey;
	}

	protected Expression getPrimaryKeyComparator(Set<String> pkColumns) {
		if (pkColumns.size() < 1) {
			throw new DataaccessException("Primary key not specified");
		}
		int i = 0;
		Conjunction comparator = null;
		for (String column: pkColumns) {
			if (i++ == 0) {
				 Expression firstExpr = new Equal(column);
				 // A wee bit of optimization if key size is one.
				 if (pkColumns.size() == 1) {
					 return firstExpr;
				 }
				 comparator = new Conjunction(firstExpr);
			}
			else {
				Expression key = new Equal(column);
				comparator.and(key);
			}
		}
		return comparator;
	}
	
	protected List<Binding> getKeyBindings(Map<String, Field> primaryKey, T object) throws Exception {
		List<Binding> bindings = new ArrayList<>();
		Set<String> keyNames = primaryKey.keySet();
		for (String key : keyNames) {
			Field pk = primaryKey.get(key);
			DbField fieldAnnotation = pk.getAnnotation(DbField.class);
			if (fieldAnnotation != null) {
				pk.setAccessible(true);
				bindings.add(getBinding(object, pk, fieldAnnotation));
			}
		}
		return bindings;
	}

	protected List<Binding> getKeyBindings(Map<String, Object> primaryKey) {
		List<Binding> bindings = new ArrayList<>();
		Set<String> keyNames = primaryKey.keySet();
		for (String key : keyNames) {
			bindings.add(new Binding(primaryKey.get(key)));
		}
		return bindings;
	}
	
	protected Resource getResource() {
		return new Resource(this.tableDescriptor.value());
	}
	
	protected Resource getResource(String alias) {
		return new Resource(this.tableDescriptor.value(), alias);
	}
	
	protected Resource getResource(Class<?> dbServiceClass) {
		return new Resource(getTable(dbServiceClass).value());
	}

	protected Resource getResource(Class<?> dbServiceClass, String alias) {
		return new Resource(getTable(dbServiceClass).value(), alias);
	}
	
	protected UpdateAssignment getReferencedValue(String foreignName, String referencedName, Object referencedObject) throws IllegalArgumentException, IllegalAccessException  {
		Field referencedField = findDeclaredField(referencedName, referencedObject);
		if (referencedField == null) {
			return null;
		}
		referencedField.setAccessible(true);
		Binding binding = new Binding(referencedField.get(referencedObject));
		return new UpdateAssignment(foreignName, binding);
	}

	private Field findDeclaredField(String referencedName, Object referencedObject) {
		Class<?> referenceClass = referencedObject.getClass();
		while (referenceClass != null) {
			try {
				return referencedObject.getClass().getDeclaredField(referencedName);
			} 
			catch (NoSuchFieldException e) {
			}
			referenceClass = referenceClass.getSuperclass();
		}
		return null;
	}
}
