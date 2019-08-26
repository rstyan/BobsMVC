package com.clickability.cms.dataaccess.sqlbuilder

class Limit : Expression {

    private var offset: Integer? = null
    private var limit: Integer? = null

    constructor() {
        this.offset = 0
        this.limit = 1
    }

    constructor(limit: Integer) {
        this.offset = 0
        this.limit = limit
    }

    fun offset(offset: Integer): Limit {
        this.offset = offset
        return this
    }

    @Override
    fun get(): String {
        return String.format("limit %s, %s", this.offset, this.limit)
    }

}
