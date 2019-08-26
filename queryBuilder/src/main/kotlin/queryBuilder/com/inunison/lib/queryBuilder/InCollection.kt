package com.clickability.cms.dataaccess.sqlbuilder

import java.util.StringJoiner

class InCollection<T>() : AbstractCompare<Equal>() {

    private var collection: Collection<T>? = null

    init {
        this.using("in")
    }

    constructor(lhs: String, rhs: Collection<T>) : this() {
        lhs(lhs)
        this.collection = rhs
    }

    constructor(lhs: Column, rhs: Collection<T>) : this() {
        lhs(lhs)
        this.collection = rhs
    }

    @Override
    fun get(): String {
        return String.format("(%s)%s(%s)", this.lhs, this.op, this.getValues(collection!!))
    }

    private fun getValues(collection: Collection<T>): String {
        val sb = StringJoiner(",")
        for (value in collection) {
            sb.add(value.toString())
        }
        return sb.toString()
    }

}
