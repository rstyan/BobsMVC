package com.clickability.cms.dataaccess.sqlbuilder

class Query : Statement {

    protected var select: Selection
    protected var from: Joinable? = null
    protected var where: Expression
    protected var groupBy: GroupBy? = null
    protected var orderBy: OrderBy? = null
    protected var forUpdate: Boolean = false

    init {
        this.select = Selection().add("*")
        // Is there a better default for this?
        this.from = null
        this.where = EmptyExpression()
        this.groupBy = null
        this.orderBy = null
        this.forUpdate = false
    }

    fun select(selection: Selection): Query {
        this.select = selection
        return this
    }

    fun from(from: Joinable): Query {
        this.from = from
        return this
    }

    /**
     * Returns a conjunction of the input criteria
     * For disjunctions use where(new Disjunction(....).or(...))
     */
    fun where(vararg criteria: Expression): Query {
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

    fun orderBy(order: OrderBy): Query {
        this.orderBy = order
        return this
    }

    fun groupBy(group: GroupBy): Query {
        this.groupBy = group
        return this
    }

    fun forUpdate(): Query {
        this.forUpdate = true
        return this
    }

    @Override
    fun get(): String {
        var base = String.format("select %s from %s where %s", this.select.get(), this.from!!.get(), this.where.get())
        if (this.groupBy != null) {
            base = String.format("%s %s", base, this.groupBy!!.get())
        }
        if (this.orderBy != null) {
            base = String.format("%s %s", base, this.orderBy!!.get())
        }
        if (this.forUpdate) {
            base = String.format("%s %s", base, "FOR UPDATE")
        }
        return base
    }

}
