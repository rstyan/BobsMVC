package com.inunison.lib.queryBuilder

import com.inunison.lib.queryBuilder.Join.Type

internal class JoinExpression(private val resource: Joinable) : Expression {
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
    override fun get(): String {
        var base = String.format("%s (%s)", this.type!!.name, this.resource.get())
        if (this.criteria != null) {
            base += String.format(" on (%s)", this.criteria!!.get())
        }
        return base
    }
}
