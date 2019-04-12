package com.clickability.cms.dataaccess.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbField;

/**
 * Basic CRUD operations for
 * the most common case: an integer primary key
 * 
 * @author roystyan
 *
 */
public abstract class AutoIncServiceImpl<T> extends DbServiceImpl<T> implements AutoIncService<T> {
	
	protected AutoIncServiceImpl(Class<T> classT) {
		super(classT);
	}
	
	@Override
	public T findById(int id) throws Exception {
		Map<String,Object> primaryKey = new HashMap<String, Object>();
		primaryKey.put(getPrimaryKeyName(), id);
		return findById(primaryKey);
	}
	
	@Override
	public void delete(int id) throws Exception {
		Map<String,Object> primaryKey = new HashMap<String, Object>();
		primaryKey.put(getPrimaryKeyName(), id);
		delete(primaryKey);
	}

	private String getPrimaryKeyName() {
		for (Field field : getAllDbFields(this.classT)) {
			DbField columnDescriptor = field.getAnnotation(DbField.class);
			if (columnDescriptor.primary()) {
				return columnDescriptor.value();
			}
		}
		return null;
	}
	
}
