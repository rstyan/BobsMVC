package com.clickability.cms.dataaccess.service;

import java.util.Map;

public interface DbService<T> {
	
	// A generic finder for an arbitrary set of primary keys.
	// Be good to your users and only use this method to implement
	// a query with the key explicitly in the method signature
	// i.e. findById(int key1, Date key2, long key3);
	T findById(Map<String, Object> primaryKey) throws Exception;

	void delete(T instance) throws Exception;
	
	T insert(T instance) throws Exception;

	void update(T instance) throws Exception;


}
