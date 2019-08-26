package com.inunison.lib.queryBuilder

class Disjunction(private val first: Expression) : Expression {

    private val predicates: MutableList<Expression> = mutableListOf()

    fun or(vararg clauses: Expression): Disjunction {
        for (c in clauses) {
            this.predicates.add(c)
        }
        return this
    }

    fun size(): Int = this.predicates.size + 1

    override fun get(): String {
        var result = first.get()
        for (predicate in this.predicates) {
            result += String.format(" or (%s)", predicate.get())
        }
        return result
    }
}
