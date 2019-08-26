package com.clickability.cms.dataaccess.sqlbuilder

class Compare() : AbstractCompare<Compare>() {

    constructor(lhs: String, op: String) : this() {
        lhs(lhs)
        using(op)
    }

    constructor(lhs: String, op: String, rhs: String) : this(lhs, op) {
        rhs(rhs)
    }

}
