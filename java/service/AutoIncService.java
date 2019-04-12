package com.clickability.cms.dataaccess.service;

/**
 * Basic CRUD operations for
 * the most common case: an integer primary key
 */
public interface AutoIncService<T> extends DbService<T> {
	
	T findById(int id) throws Exception;

	void delete(int id) throws Exception;
	
}
