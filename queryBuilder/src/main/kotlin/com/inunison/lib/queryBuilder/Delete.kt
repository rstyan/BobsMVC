package com.inunison.lib.queryBuilder

internal open class Delete protected constructor(
        private val from: Resource,
        private var where: Expression = EmptyExpression()
) : Statement {

    /**
     * Returns a conjunction of the input criteria
     * For disjunctions use where(new Disjunction(....).or(...))
     */
    fun where(vararg criteria: Expression): Delete {
        if (criteria.isEmpty()) {
            this.where = EmptyExpression()
        } else if (criteria.size == 1) {
            this.where = criteria[0]
        } else {
            val conjunction = Conjunction(criteria[0])
            for (i in 1 until criteria.size) {
                conjunction.and(criteria[i])
            }
            this.where = conjunction
        }
        return this
    }

    override fun get(): String {
        return String.format("delete from %s where %s", this.from.get(), this.where.get())
    }
}
