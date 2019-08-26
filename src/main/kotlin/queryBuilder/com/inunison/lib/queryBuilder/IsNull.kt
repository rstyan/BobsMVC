package com.clickability.cms.dataaccess.sqlbuilder

class IsNull(private val fieldName: String) : Expression {

    fun get(): String {
        return String.format("%s is NULL", this.fieldName)
    }
}
