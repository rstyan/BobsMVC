package com.inunison.lib.queryBuilder

internal class Limit(
        private var offset: Int = 0,
        private var limit: Int = 0
) : Expression {

    fun offset(offset: Int): Limit {
        this.offset = offset
        return this
    }

    override fun get(): String {
        return String.format("limit %s, %s", this.offset, this.limit)
    }
}
