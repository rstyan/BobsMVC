package com.inunison.lib.queryBuilder

import java.util.StringJoiner

internal class Selection(
        private val base: Resource,
        private val selectList: StringJoiner = StringJoiner(","),
        private var size: Int = 0
) : Expression {

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

    fun size(): Int = this.size

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
    override fun get(): String {
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
