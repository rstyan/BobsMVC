package com.clickability.cms.dataaccess.sqlbuilder

import java.util.ArrayList
import java.util.StringJoiner

import com.clickability.cms.dataaccess.sqlbuilder.Query
import com.clickability.cms.dataaccess.sqlbuilder.Resource

class Insert(private val resource: Resource) : Query() {
    val data: List<UpdateAssignment>

    init {
        this.data = ArrayList<UpdateAssignment>()
    }

    fun add(value: UpdateAssignment): Insert {
        this.data.add(value)
        return this
    }

    fun get(): String {
        val columns = StringJoiner(",")
        val values = StringJoiner(",")
        for (assignment in this.data) {
            columns.add(assignment.column)
            values.add("?")
        }
        return String.format("insert into %s (%s) values (%s)", resource.get(), columns.toString(), values.toString())
    }

}
