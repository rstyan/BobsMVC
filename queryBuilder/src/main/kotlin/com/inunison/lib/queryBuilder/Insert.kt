package com.inunison.lib.queryBuilder

import java.util.*

internal class Insert(private val resource: Resource) : Query() {
    val data: MutableList<UpdateAssignment>

    init {
        this.data = ArrayList<UpdateAssignment>()
    }

    fun add(value: UpdateAssignment): Insert {
        this.data.add(value)
        return this
    }

    override fun get(): String {
        val columns = StringJoiner(",")
        val values = StringJoiner(",")
        for (assignment in this.data) {
            columns.add(assignment.column)
            values.add("?")
        }
        return String.format("insert into %s (%s) values (%s)", resource.get(), columns.toString(), values.toString())
    }
}
