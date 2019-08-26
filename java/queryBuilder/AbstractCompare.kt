package com.clickability.cms.dataaccess.sqlbuilder

abstract class AbstractCompare<T> : Expression {

    protected var lhs: String
    protected var op: String
    protected var rhs: String

    init {
        this.lhs = "1"
        this.op = "="
        this.rhs = "?"
    }

    fun using(op: String): AbstractCompare<T> {
        this.op = op
        return this
    }

    fun lhs(operand: String): AbstractCompare<T> {
        this.lhs = operand
        return this
    }

    fun lhs(operand: Column): AbstractCompare<T> {
        return this.lhs(operand.get())
    }

    fun rhs(operand: String): AbstractCompare<T> {
        this.rhs = String.format("'%s'", operand)
        return this
    }

    private fun add(operand: String): AbstractCompare<T> {
        this.rhs = operand
        return this
    }

    fun rhs(operand: Column): AbstractCompare<T> {
        return this.add(operand.get())
    }

    /*
	 * TODO - add more RHS type as needed.
	 */
    fun rhs(operand: Integer): AbstractCompare<T> {
        return this.add(operand.toString())
    }

    fun rhs(operand: Long): AbstractCompare<T> {
        return this.add(operand.toString())
    }

    fun rhs(operand: Double): AbstractCompare<T> {
        return this.add(operand.toString())
    }

    fun rhs(operand: Float): AbstractCompare<T> {
        return this.add(operand.toString())
    }

    fun rhs(q: Query): AbstractCompare<T> {
        return this.add(q.get())
    }

    fun get(): String {
        return String.format("(%s)%s(%s)", this.lhs, this.op, this.rhs)
    }

}
