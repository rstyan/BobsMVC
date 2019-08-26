package com.inunison.lib.queryBuilder

internal class IsNull(private val fieldName: String) : Expression {

    override fun get(): String {
        return String.format("%s is NULL", this.fieldName)
    }
}
