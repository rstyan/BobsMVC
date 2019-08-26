package com.clickability.cms.dataaccess.service

/**
 * Basic CRUD operations for
 * the most common case: an integer primary key
 */
interface AutoIncService<T> : DbService<T> {

    @Throws(Exception::class)
    fun findById(id: Int): T

    @Throws(Exception::class)
    fun delete(id: Int)

}
