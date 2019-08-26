package com.inunison.lib.queryBuilder

internal class Compare() : AbstractCompare<Compare>() {

    constructor(lhs: String, op: String) : this() {
        lhs(lhs)
        using(op)
    }

    constructor(lhs: String, op: String, rhs: String) : this(lhs, op) {
        rhs(rhs)
    }

}