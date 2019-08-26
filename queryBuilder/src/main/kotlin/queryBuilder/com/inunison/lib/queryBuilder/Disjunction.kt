package com.clickability.cms.dataaccess.sqlbuilder

import java.util.ArrayList

class Disjunction(private val first: Expression) : Expression {

    private val predicates: List<Expression>

    init {
        this.predicates = ArrayList<Expression>()
    }

    fun or(vararg clauses: Expression): Disjunction {
        for (c in clauses) {
            this.predicates.add(c)
        }
        return this
    }

    fun size(): Integer {
        return this.predicates.size() + 1
    }

    @Override
    fun get(): String {
        var result = first.get()
        for (predicate in this.predicates) {
            result += String.format(" or (%s)", predicate.get())
        }
        return result
    }

}
