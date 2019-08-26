package com.inunison.lib.queryBuilder

internal class Conjunction(private val first: Expression) : Expression {

    private val predicates: MutableList<Expression>

    init {
        this.predicates = mutableListOf()
    }

    fun and(vararg clauses: Expression): Conjunction {
        for (c in clauses) {
            this.predicates.add(c)
        }
        return this
    }

    fun size(): Int {
        return this.predicates.size + 1
    }

    @Override
    override fun get(): String {
        var result = first.get()
        for (predicate in this.predicates) {
            result += String.format(" and (%s)", predicate.get())
        }
        return result
    }

}
