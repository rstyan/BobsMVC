package com.inunison.lib.queryBuilder

import java.util.StringJoiner

internal class MultiDelete : Delete() {

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
    override fun get(): String {
        return String.format("delete %s from %s where %s", this.tables.toString(), this.from.get(), this.where.get())
    }

}
