package com.clickability.cms.dataaccess.service

interface DbService<T> {

    // A generic finder for an arbitrary set of primary keys.
    // Be good to your users and only use this method to implement
    // a query with the key explicitly in the method signature
    // i.e. findById(int key1, Date key2, long key3);
    @Throws(Exception::class)
    fun findById(primaryKey: Map<String, Object>): T

    @Throws(Exception::class)
    fun delete(instance: T)

    @Throws(Exception::class)
    fun insert(instance: T): T

    @Throws(Exception::class)
    fun update(instance: T)


}
