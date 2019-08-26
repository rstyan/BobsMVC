package com.clickability.cms.dataaccess.sqlbuilder

import java.util.StringJoiner

class Selection() : Expression {

    private val selectList: StringJoiner
    private val base: Resource?
    private var size: Int = 0

    init {
        this.selectList = StringJoiner(",")
        this.size = 0
    }

    constructor(base: Resource) : this() {
        this.base = base
    }

    constructor(vararg columns: String) : this() {
        for (column in columns) {
            this.append(column)
        }
    }

    constructor(vararg columns: Column) : this() {
        for (column in columns) {
            this.append(column.get())
        }
    }

    fun size(): Int {
        return this.size
    }

    protected fun append(item: String) {
        this.size += 1
        this.selectList.add(item)
    }

    fun add(vararg columns: String): Selection {
        for (column in columns) {
            this.append(column)
        }
        return this
    }

    fun add(vararg columns: Column): Selection {
        for (column in columns) {
            this.append(column.get())
        }
        return this
    }

    @Override
    fun get(): String {
        var selection = ""
        if (this.base != null) {
            selection += this.base!!.alias()
        }
        if (this.size > 0) {
            selection += this.selectList.toString()
        }
        return selection
    }

}
