package com.clickability.cms.dataaccess.sqlbuilder

import java.util.StringJoiner

class OrderBy() : Expression {

    private val orderby: StringJoiner

    enum class Direction private constructor(val name: String) {
        ASC("asc"), DESC("desc")
    }

    init {
        this.orderby = StringJoiner(",")
    }

    constructor(column: String) : this() {
        this.add(column)
    }

    constructor(column: String, dir: Direction) : this() {
        this.add(column, dir)
    }

    constructor(column: Column) : this() {
        this.add(column)
    }

    constructor(column: Column, dir: Direction) : this() {
        this.add(column, dir)
    }

    fun add(column: String): OrderBy {
        this.add(Column(column), Direction.ASC)
        return this
    }

    fun add(column: String, dir: Direction): OrderBy {
        this.add(Column(column), dir)
        return this
    }

    fun add(column: Column): OrderBy {
        this.add(column, Direction.ASC)
        return this
    }

    fun add(column: Column, direction: Direction): OrderBy {
        this.orderby.add(column.get() + " " + direction.name)
        return this
    }

    @Override
    fun get(): String {
        return String.format("order by %s", this.orderby.toString())
    }

}
