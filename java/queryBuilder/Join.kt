package com.clickability.cms.dataaccess.sqlbuilder

import java.util.ArrayList

class Join(private val base: Joinable) : Joinable {

    private val joins: List<JoinExpression>

    enum class Type private constructor(val name: String) {
        INNER("inner join"), OUTER("left outer join"),
        RIGHT_INNER("right inner join"), RIGHT_OUTER("right outer join")
    }

    init {
        this.joins = ArrayList<JoinExpression>()
    }

    fun join(vararg expressions: JoinExpression): Join {
        for (expr in expressions) {
            this.joins.add(expr)
        }
        return this
    }

    @Override
    fun get(): String {
        var join = String.format("(%s)", this.base.get())
        for (expr in this.joins) {
            join += " " + expr.get()
        }
        return join
    }

}
