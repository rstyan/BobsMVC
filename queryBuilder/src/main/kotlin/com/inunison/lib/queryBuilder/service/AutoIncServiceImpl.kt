package com.clickability.cms.dataaccess.service

import java.lang.reflect.Field
import java.util.HashMap

import com.inunison.lib.queryBuilder.annotations.DbField

/**
 * Basic CRUD operations for
 * the most common case: an integer primary key
 *
 * @author roystyan
 */
abstract class AutoIncServiceImpl<T> protected constructor(classT: Class<T>) : DbServiceImpl<T>(classT), AutoIncService<T> {

    private val primaryKeyName: String?
        get() {
            for (field in getAllDbFields(this.classT)) {
                val columnDescriptor = field.getAnnotation(DbField::class.java)
                if (columnDescriptor.primary()) {
                    return columnDescriptor.value()
                }
            }
            return null
        }

    @Override
    @Throws(Exception::class)
    fun findById(id: Int): T {
        val primaryKey = HashMap<String, Object>()
        primaryKey.put(primaryKeyName, id)
        return findById(primaryKey.toInt())
    }

    @Override
    @Throws(Exception::class)
    fun delete(id: Int) {
        val primaryKey = HashMap<String, Object>()
        primaryKey.put(primaryKeyName, id)
        delete(primaryKey.toInt())
    }

}
