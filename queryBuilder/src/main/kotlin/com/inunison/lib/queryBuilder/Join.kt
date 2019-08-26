package com.inunison.lib.queryBuilder

import java.util.ArrayList

internal class Join(private val base: Joinable) : Joinable {

    private val joins: MutableList<JoinExpression>

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

    override fun get(): String {
        var join = String.format("(%s)", this.base.get())
        for (expr in this.joins) {
            join += " " + expr.get()
        }
        return join
    }

}
