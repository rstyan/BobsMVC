package com.clickability.cms.dataaccess.sqlbuilder

import java.util.StringJoiner

class GroupBy() : Expression {

    private val groupBy: StringJoiner

    init {
        this.groupBy = StringJoiner(",")
    }

    constructor(column: Column) : this() {
        add(column)
    }

    constructor(column: String) : this() {
        add(column)
    }

    fun add(column: Column): GroupBy {
        this.groupBy.add(column.get())
        return this
    }

    fun add(column: String): GroupBy {
        this.groupBy.add(column)
        return this
    }

    @Override
    fun get(): String {
        return String.format("group by %s", this.groupBy.toString())
    }


}
