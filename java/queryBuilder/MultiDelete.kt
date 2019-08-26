package com.clickability.cms.dataaccess.sqlbuilder

import java.util.StringJoiner

class MultiDelete : Delete() {

    private val tables: StringJoiner

    init {
        this.tables = StringJoiner(",")
    }

    fun add(table: Resource): MultiDelete {
        tables.add(table.alias())
        return this
    }

    fun where(where: Expression): MultiDelete {
        this.where = where
        return this
    }

    fun from(from: Joinable): MultiDelete {
        this.from = from
        return this
    }

    @Override
    fun get(): String {
        return String.format("delete %s from %s where %s", this.tables.toString(), this.from.get(), this.where.get())
    }

}
