package com.clickability.cms.dataaccess.sqlbuilder

import java.util.ArrayList
import java.util.StringJoiner

import com.clickability.cms.dataaccess.sqlbuilder.Query
import com.clickability.cms.dataaccess.sqlbuilder.Resource

class Update(private val resource: Resource) : Query() {
    protected val assignments: List<UpdateAssignment>
    protected var where: Expression

    init {
        this.assignments = ArrayList()
        this.where = EmptyExpression()
    }

    fun add(vararg values: UpdateAssignment): Update {
        for (value in values) {
            this.assignments.add(value)
        }
        return this
    }

    /**
     * Returns a conjunction of the input criteria
     * For disjunctions use where(new Disjunction(....).or(...))
     */
    fun where(vararg criteria: Expression): Update {
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

    fun get(): String {
        val sj = StringJoiner(",")
        for (set in this.assignments) {
            sj.add(set.get())
        }
        return String.format("update %s set %s where %s", resource.get(), sj.toString(), where.get())
    }

}
