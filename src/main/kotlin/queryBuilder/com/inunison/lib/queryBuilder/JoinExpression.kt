package com.clickability.cms.dataaccess.sqlbuilder

import com.clickability.cms.dataaccess.sqlbuilder.Join.Type

class JoinExpression(private val resource: Joinable) : Expression {
    private var type: Type? = null
    private var criteria: Expression? = null

    init {
        this.type = Join.Type.INNER
        this.criteria = null
    }

    fun using(type: Join.Type): JoinExpression {
        this.type = type
        return this
    }

    fun on(criteria: Expression): JoinExpression {
        this.criteria = criteria
        return this
    }

    @Override
    fun get(): String {
        var base = String.format("%s (%s)", this.type!!.name, this.resource.get())
        if (this.criteria != null) {
            base += String.format(" on (%s)", this.criteria!!.get())
        }
        return base
    }

}
