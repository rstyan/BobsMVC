package com.inunison.lib.queryBuilder

internal class Equal() : AbstractCompare<Equal>() {

    init {
        this.using("=")
    }

    constructor(lhs: Column, rhs: Column) : this() {
        lhs(lhs)
        rhs(rhs)
    }

    constructor(lhs: Column) : this() {
        lhs(lhs)
    }

    constructor(lhs: String) : this() {
        lhs(lhs)
    }

    constructor(lhs: String, rhs: String) : this() {
        lhs(lhs)
        rhs(rhs)
    }

    constructor(lhs: String, rhs: Int) : this() {
        lhs(lhs)
        rhs(rhs)
    }
}
