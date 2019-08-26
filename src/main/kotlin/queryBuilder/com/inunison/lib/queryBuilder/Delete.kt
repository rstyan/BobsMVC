package com.clickability.cms.dataaccess.sqlbuilder

class Delete protected constructor() : Statement {

    protected var from: Joinable
    protected var where: Expression

    constructor(from: Resource) : this() {
        this.from = from
    }

    init {
        this.where = EmptyExpression()
    }

    /**
     * Returns a conjunction of the input criteria
     * For disjunctions use where(new Disjunction(....).or(...))
     */
    fun where(vararg criteria: Expression): Delete {
        if (criteria.size < 1) {
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

    @Override
    fun get(): String {
        return String.format("delete from %s where %s", this.from.get(), this.where.get())
    }

}
