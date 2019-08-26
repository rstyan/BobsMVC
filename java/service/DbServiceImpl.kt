package com.clickability.cms.dataaccess.service

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import java.util.HashMap
import java.util.function.Function

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger

import com.clickability.cms.dataaccess.sqlbuilder.Binding
import com.clickability.cms.dataaccess.sqlbuilder.UpdateAssignment
import com.clickability.cms.dataaccess.sqlbuilder.Conjunction
import com.clickability.cms.dataaccess.sqlbuilder.DataaccessException
import com.clickability.cms.dataaccess.sqlbuilder.Delete
import com.clickability.cms.dataaccess.sqlbuilder.Insert
import com.clickability.cms.dataaccess.sqlbuilder.Equal
import com.clickability.cms.dataaccess.sqlbuilder.Expression
import com.clickability.cms.dataaccess.sqlbuilder.Query
import com.clickability.cms.dataaccess.sqlbuilder.QueryManager
import com.clickability.cms.dataaccess.sqlbuilder.Resource
import com.clickability.cms.dataaccess.sqlbuilder.UpdateManager
import com.clickability.cms.dataaccess.sqlbuilder.TransactionManagerFactory
import com.clickability.cms.dataaccess.sqlbuilder.Update
import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbField
import com.clickability.cms.dataaccess.sqlbuilder.annotations.Reference
import com.clickability.cms.dataaccess.sqlbuilder.annotations.Table
import com.clickability.dbmanager.DBManager

/**
 * A generic service class for basic CRUD operations.
 *
 * @author roystyan
 */
abstract class DbServiceImpl<T> protected constructor(protected val classT: Class<T>) : DbService<T> {
    protected val tableDescriptor: Table

    protected val table: Table
        get() = getTable(this.classT)

    protected val databaseManager: DBManager
        get() = DBManager.getDBManager(tableDescriptor.database())

    protected val primaryKey: Map<String, Field>
        get() {
            val primaryKey = HashMap()
            for (field in getAllDbFields(this.classT)) {
                val fieldAnnotation = field.getAnnotation(DbField::class.java)
                if (fieldAnnotation != null && fieldAnnotation!!.primary()) {
                    field.setAccessible(true)
                    primaryKey.put(fieldAnnotation!!.value(), field)
                }
            }
            if (primaryKey.size() < 1) {
                throw DataaccessException("Missing primary key annotation")
            }
            return primaryKey
        }

    protected val resource: Resource
        get() = Resource(this.tableDescriptor.value())

    init {
        this.tableDescriptor = this.table
    }// TODO  This sucks.  Normally the ParameterizedType technique would work, but not when we try to bind an intercepter
    // to handle @Transactional.  So subclasses will have to tell us what the paramaterized type is.
    // can we get around this?
    // this.classT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Override
    @Throws(Exception::class)
    fun findById(primaryKey: Map<String, Object>): T? {
        val query = Query().from(resource).where(getPrimaryKeyComparator(primaryKey.keySet()))
        return fetch(query, getKeyBindings(primaryKey))
    }

    @Throws(Exception::class)
    protected fun fetchAll(query: Query, vararg bindings: Binding): List<T> {
        QueryManager(this.databaseManager).use({ qm -> return qm.execute(query, bindings) { rs -> loadResults(rs, this.classT) } })
    }

    @Throws(Exception::class)
    protected fun fetchAll(query: Query, bindings: List<Binding>): List<T> {
        QueryManager(this.databaseManager).use({ qm -> return qm.execute(query, bindings) { rs -> loadResults(rs, this.classT) } })
    }

    @Throws(Exception::class)
    protected fun fetchAll(query: Query): List<T> {
        val bindings = ArrayList()
        return fetchAll(query, bindings)
    }

    @Throws(SQLException::class)
    protected fun <R> fetchAll(query: Query, bindings: Array<Binding>, loader: Function<ResultSet, R>): List<R> {
        QueryManager(this.databaseManager).use({ qm -> return qm.execute(query, bindings, loader) })
    }

    @Throws(SQLException::class)
    protected fun <R> fetchAll(query: Query, bindings: List<Binding>, loader: Function<ResultSet, R>): List<R> {
        QueryManager(this.databaseManager).use({ qm -> return qm.execute(query, bindings, loader) })
    }

    @Throws(Exception::class)
    protected fun fetch(query: Query, vararg bindings: Binding): T? {
        QueryManager(this.databaseManager).use({ qm ->
            val results = qm.execute(query, bindings) { rs -> loadResults(rs, this.classT) }
            return if (results.size() === 1) results.get(0) else null
        })
    }

    @Throws(Exception::class)
    protected fun fetch(query: Query, bindings: List<Binding>): T? {
        return fetch(query, bindings, this.classT)
    }

    @Throws(Exception::class)
    protected fun <R> fetch(query: Query, bindings: List<Binding>, classR: Class<R>): R? {
        QueryManager(this.databaseManager).use({ qm ->
            val results = qm.execute(query, bindings) { rs -> loadResults(rs, classR) }
            return if (results.size() === 1) results.get(0) else null
        })
    }

    @Throws(Exception::class)
    protected fun fetch(query: Query): T? {
        val bindings = ArrayList<Binding>()
        return fetch(query, bindings)
    }

    protected fun <R> loadResults(rs: ResultSet, classR: Class<R>): R {
        val `object` = newInstance<Object>(classR)
        for (field in getAllDbFields(classR)) {
            val annotation = field.getAnnotation(DbField::class.java)
            if (annotation != null) {
                try {
                    val value = rs.getObject(annotation!!.value())
                    setValue(`object`, value, field, annotation!!.mapsto())
                } catch (e: Exception) {
                    logger.error(String.format("exception on field %s", annotation!!.value()))
                    throw DataaccessException(e)
                }

            }
        }
        for (field in this.getAllReferences(classR)) {
            val annotation = field.getAnnotation(Reference::class.java)
            if (annotation != null) {
                try {
                    val primaryValue = rs.getObject(annotation!!.foreignKey())
                    if (primaryValue != null) {
                        val primaryKey = HashMap()
                        primaryKey.put(annotation!!.referencedField(), primaryValue)
                        val referencedClass = field.getType()
                        val referencedTable = getResource(referencedClass)
                        if (referencedTable != null) {
                            val pkComparator = getPrimaryKeyComparator(primaryKey.keySet())
                            val query = Query().from(referencedTable).where(pkComparator)
                            val bindings = getKeyBindings(primaryKey)
                            val value = fetch<Object>(query, bindings, referencedClass)
                            setValue(`object`, value, field, Void.TYPE)
                        }
                    }
                } catch (e: Exception) {
                    logger.error(String.format("exception on reference %s", annotation!!.foreignKey()))
                    throw DataaccessException(e)
                }

            }
        }
        return `object`
    }

    @Throws(IllegalAccessException::class, IllegalArgumentException::class, InvocationTargetException::class)
    private operator fun <R> setValue(`object`: R, value: Object?, field: Field, parameterType: Class<*>) {
        var value = value
        var parameterType = parameterType
        var setter: Method? = null
        if (!parameterType.equals(Void.TYPE)) {
            setter = getSetter(`object`.getClass(), field, parameterType)
        }
        if (setter == null) {
            parameterType = field.getType()
            setter = getSetter(`object`.getClass(), field, parameterType)
        }
        value = Binding.fromSqlType(value, field.getType())
        val unsafeSet = isSetValueUnsafe(value, field)

        if (!unsafeSet) {
            if (setter != null) {
                setter!!.setAccessible(true)
                setter!!.invoke(`object`, value)
            } else {
                field.setAccessible(true)
                field.set(`object`, value)
            }
        } else {
            logger.debug(if ("Can't set field '" + field.getName() + "' to: " + value != null) value else "null")
        }
    }

    private fun getSetter(objectClass: Class<*>, field: Field, parameterType: Class<*>): Method? {
        val prefixes = arrayOf("to", "set")
        val fieldName = StringUtils.capitalize(field.getName())
        for (prefix in prefixes) {
            var setter: Method? = null
            try {
                setter = objectClass.getDeclaredMethod(prefix + fieldName, parameterType)
                if (setter!!.getReturnType().equals(Void.TYPE)) {
                    return setter
                }
            } catch (e: Exception) {
                // no such method.  not a problem.
            }

        }
        // look no further than the class containing the declaring field.
        if (objectClass.equals(field.getDeclaringClass())) {
            return null
        }
        val superClass = objectClass.getSuperclass()
        return if (superClass != null) {
            getSetter(superClass, field, parameterType)
        } else null
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
    private fun <R> newInstance(classR: Class<R>): R {
        @SuppressWarnings("unchecked")
        val ctors = classR.getDeclaredConstructors() as Array<Constructor<R>>
        var ctor: Constructor<R>? = null
        for (i in ctors.indices) {
            ctor = ctors[i]
            if (ctor!!.getGenericParameterTypes().length === 0) break
        }

        if (ctor == null) {
            throw DataaccessException("Missing empty constructor")
        }

        ctor!!.setAccessible(true)
        try {
            return ctor!!.newInstance()
        } catch (e: Exception) {
            throw DataaccessException(e)
        }

    }

    @Override
    @Throws(IllegalArgumentException::class, IllegalAccessException::class, Exception::class)
    fun delete(`object`: T) {
        delete(getPrimaryKey(`object`))
    }

    @Throws(Exception::class)
    protected fun delete(primaryKey: Map<String, Object>) {
        val delExpr = Delete(Resource(tableDescriptor.value())).where(this.getPrimaryKeyComparator(primaryKey.keySet()))
        TransactionManagerFactory.get(tableDescriptor.database()).use({ um ->
            val bindings = getKeyBindings(primaryKey)
            um.execute(delExpr, bindings.toArray(arrayOfNulls<Binding>(bindings.size())))
        })
    }

    @Override
    @Throws(Exception::class)
    fun insert(instance: T): T {
        val insert = Insert(Resource(tableDescriptor.value()))
        for (field in getAllDbFields(instance.getClass())) {
            val annotation = field.getAnnotation(DbField::class.java)
            if (!annotation.autogen()) {
                field.setAccessible(true)
                insert.add(UpdateAssignment(annotation.value(), getBinding(instance, field, annotation)))
            }
        }

        for (field in getAllReferences(instance.getClass())) {
            field.setAccessible(true)
            val annotation = field.getAnnotation(Reference::class.java)

            // e.g. if the field is "customer" then this statement would be equivalent to:
            // Customer referencedObject = instance.getCustomer();
            val referencedObject = field.get(instance)

            if (referencedObject != null) {
                val value = getReferencedValue(annotation.foreignKey(), annotation.referencedField(), referencedObject)
                if (value != null) {
                    insert.add(value)
                }
            }
        }

        TransactionManagerFactory.get(tableDescriptor.database()).use({ qm ->
            val primaryKey = primaryKey
            val isAutogen = isAutogen(primaryKey, instance)
            val newId = qm.execute(insert, isAutogen)
            if (isAutogen) {
                for (fieldName in primaryKey.keySet()) {
                    val field = primaryKey[fieldName]
                    field.setAccessible(true)
                    field.set(instance, newId)
                }
            }
        })
        return instance
    }

    @Throws(IllegalArgumentException::class, IllegalAccessException::class)
    private fun isAutogen(pk: Map<String, Field>, instance: T): Boolean {
        // there can only be one.
        if (pk.size() === 1) {
            for (name in pk.keySet()) {
                val field = pk[name]
                val annotation = field.getAnnotation(DbField::class.java)
                field.setAccessible(true)
                val fieldValue = field.get(instance)
                return annotation.autogen() && isUnassigned(fieldValue)
            }
        }
        return false
    }

    private fun isUnassigned(value: Object?): Boolean {
        return value == null || value is Integer && value as Integer? < 1 || value is Long && value as Long? < 1
    }

    @Override
    @Throws(Exception::class)
    fun update(`object`: T) {
        val primaryKey = primaryKey
        val update = Update(Resource(tableDescriptor.value())).where(this.getPrimaryKeyComparator(primaryKey.keySet()))
        for (field in getAllDbFields(`object`.getClass())) {
            val annotation = field.getAnnotation(DbField::class.java)
            if (!annotation.autogen() && !annotation.primary() && annotation.mutable()) {
                field.setAccessible(true)
                update.add(UpdateAssignment(annotation.value(), getBinding(`object`, field, annotation)))
            }
        }

        TransactionManagerFactory.get(tableDescriptor.database()).use({ qm ->
            val bindings = getKeyBindings(primaryKey, `object`)
            qm.execute(update, bindings.toArray(arrayOfNulls<Binding>(bindings.size())))
        })
    }

    @Throws(Exception::class)
    private fun getBinding(`object`: T, field: Field, descriptor: DbField): Binding {
        var getter: Method? = null
        var value: Object? = null
        var parameterType = descriptor.mapsto()
        // void means mapsto was not set, so ignore it.
        if (!parameterType.equals(Void.TYPE)) {
            getter = getGetter(`object`.getClass(), field, parameterType)
        }
        if (getter == null) {
            parameterType = field.getType()
            getter = getGetter(`object`.getClass(), field, parameterType)
        }
        if (getter != null) {
            getter!!.setAccessible(true)
            try {
                value = getter!!.invoke(`object`)
            } catch (e: InvocationTargetException) {
                logger.error("Error invoking Getter", e.getTargetException())
                throw e
            }

        } else {
            field.setAccessible(true)
            value = field.get(`object`)
        }
        return if (value == null) Binding(parameterType) else Binding(value)
    }

    private fun getGetter(objectClass: Class<*>, field: Field, parameterType: Class<*>): Method? {
        val prefixes = arrayOf("from", "get")
        val fieldName = StringUtils.capitalize(field.getName())
        for (prefix in prefixes) {
            var getter: Method? = null
            try {
                getter = objectClass.getDeclaredMethod(prefix + fieldName)
                if (getter!!.getReturnType().equals(parameterType)) {
                    return getter
                }
            } catch (e: Exception) {
                // ignore
            }

        }
        // look no further than the class containing the declaring field.
        if (objectClass.equals(field.getDeclaringClass())) {
            return null
        }
        val superClass = objectClass.getSuperclass()
        return if (superClass != null) {
            getGetter(superClass, field, parameterType)
        } else null
    }

    // Assumes table is declared on the current class
    // i.e.  not a superclass.  Should this constraint be relaxed?
    private fun getTable(dbServiceClass: Class<*>): Table {
        return dbServiceClass.getAnnotation(Table::class.java)
                ?: throw DataaccessException("Missing @Table annotation for class " + dbServiceClass.getSimpleName())
    }

    // include inherited fields.
    protected fun getAllDbFields(clazz: Class<*>): List<Field> {
        val dbFields = ArrayList()
        for (field in clazz.getDeclaredFields()) {
            val fieldAnnotation = field.getAnnotation(DbField::class.java)
            if (fieldAnnotation != null) {
                dbFields.add(field)
            }
        }
        val parentClass = clazz.getSuperclass()
        if (parentClass != null) {
            dbFields.addAll(getAllDbFields(parentClass!!))
        }
        return dbFields
    }

    // include inherited references.
    protected fun getAllReferences(clazz: Class<*>): List<Field> {
        val references = ArrayList()
        for (field in clazz.getDeclaredFields()) {
            val refAnnotation = field.getAnnotation(Reference::class.java)
            if (refAnnotation != null) {
                references.add(field)
            }
        }
        val parentClass = clazz.getSuperclass()
        if (parentClass != null) {
            references.addAll(getAllReferences(parentClass!!))
        }
        return references
    }

    @Throws(IllegalArgumentException::class, IllegalAccessException::class)
    protected fun getPrimaryKey(`object`: T): Map<String, Object> {
        val primaryKey = HashMap()
        for (field in getAllDbFields(this.classT)) {
            val fieldAnnotation = field.getAnnotation(DbField::class.java)
            if (fieldAnnotation != null && fieldAnnotation!!.primary()) {
                field.setAccessible(true)
                primaryKey.put(fieldAnnotation!!.value(), field.get(`object`))
            }
        }
        if (primaryKey.size() < 1) {
            throw DataaccessException("Missing primary key annotation")
        }
        return primaryKey
    }

    protected fun getPrimaryKeyComparator(pkColumns: Set<String>): Expression? {
        if (pkColumns.size() < 1) {
            throw DataaccessException("Primary key not specified")
        }
        var i = 0
        var comparator: Conjunction? = null
        for (column in pkColumns) {
            if (i++ == 0) {
                val firstExpr = Equal(column)
                // A wee bit of optimization if key size is one.
                if (pkColumns.size() === 1) {
                    return firstExpr
                }
                comparator = Conjunction(firstExpr)
            } else {
                val key = Equal(column)
                comparator!!.and(key)
            }
        }
        return comparator
    }

    @Throws(Exception::class)
    protected fun getKeyBindings(primaryKey: Map<String, Field>, `object`: T): List<Binding> {
        val bindings = ArrayList()
        val keyNames = primaryKey.keySet()
        for (key in keyNames) {
            val pk = primaryKey[key]
            val fieldAnnotation = pk.getAnnotation(DbField::class.java)
            if (fieldAnnotation != null) {
                pk.setAccessible(true)
                bindings.add(getBinding(`object`, pk, fieldAnnotation!!))
            }
        }
        return bindings
    }

    protected fun getKeyBindings(primaryKey: Map<String, Object>): List<Binding> {
        val bindings = ArrayList()
        val keyNames = primaryKey.keySet()
        for (key in keyNames) {
            bindings.add(Binding(primaryKey[key]))
        }
        return bindings
    }

    protected fun getResource(alias: String): Resource? {
        return Resource(this.tableDescriptor.value(), alias)
    }

    protected fun getResource(dbServiceClass: Class<*>): Resource {
        return Resource(getTable(dbServiceClass).value())
    }

    protected fun getResource(dbServiceClass: Class<*>, alias: String): Resource {
        return Resource(getTable(dbServiceClass).value(), alias)
    }

    @Throws(IllegalArgumentException::class, IllegalAccessException::class)
    protected fun getReferencedValue(foreignName: String, referencedName: String, referencedObject: Object): UpdateAssignment? {
        val referencedField = findDeclaredField(referencedName, referencedObject) ?: return null
        referencedField.setAccessible(true)
        val binding = Binding(referencedField.get(referencedObject))
        return UpdateAssignment(foreignName, binding)
    }

    private fun findDeclaredField(referencedName: String, referencedObject: Object): Field? {
        var referenceClass = referencedObject.getClass()
        while (referenceClass != null) {
            try {
                return referencedObject.getClass().getDeclaredField(referencedName)
            } catch (e: NoSuchFieldException) {
            }

            referenceClass = referenceClass!!.getSuperclass()
        }
        return null
    }

    companion object {

        private val logger = Logger.getLogger(DbServiceImpl<*>::class.java)

        /**
         * Checks if the specified `dbValue` is safe to be set on the `javaField`.
         * Currently only handles one edge case: `nulls` cannot be set on primivites. But it's
         * possible that we might need to extend this to handle more edge cases.
         *
         * @param dbValue the value retrieved from the db that is to be set on the corresponding java field.
         * @param javaField the java field on which the value retrieved from the db is to be set
         * @return `true` if it is safe to set the value on the field or `false` otherwise.
         */
        private fun isSetValueUnsafe(dbValue: Object?, javaField: Field): Boolean {
            return dbValue == null && javaField.getType().isPrimitive()
        }
    }
}
