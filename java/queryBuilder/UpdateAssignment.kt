package com.clickability.cms.dataaccess.sqlbuilder

import java.util.Date

class UpdateAssignment private constructor(val column: String, val operand: String?, val operator: String?, val value: Binding) : Expression {

    constructor(column: String, value: Binding) : this(column, null, null, value) {}

    constructor(column: String, value: Boolean) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Boolean?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(Boolean::class.java)) {
    }

    constructor(column: String, value: Byte) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Byte?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(Byte::class.java)) {
    }

    constructor(column: String, value: Character) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Character?) : this(column, operand, operator, if (value == null) Binding(Character::class.java) else Binding(value)) {}

    constructor(column: String, value: Short) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Short?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(Short::class.java)) {
    }

    constructor(column: String, value: Integer) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Integer?) : this(column, operand, operator, if (value == null) Binding(Integer::class.java) else Binding(value)) {}

    constructor(column: String, value: Long) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Long?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(Long::class.java)) {
    }

    constructor(column: String, value: Float) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Float?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(Float::class.java)) {
    }

    constructor(column: String, value: Double) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Double?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(Double::class.java)) {
    }

    constructor(column: String, value: String) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: String?) : this(column, operand, operator, value?.let { Binding(it) }
            ?: Binding(String::class.java)) {
    }

    constructor(column: String, value: Date) : this(column, null, null, value) {}

    constructor(column: String, operand: String?, operator: String?, value: Date?) : this(column, operand, operator, if (value == null) Binding(Date::class.java) else Binding(value)) {}

    @Override
    fun get(): String {
        val sb = StringBuilder()
        sb.append(this.column)
        sb.append("=")
        if (this.operand != null) {
            sb.append(this.operand)
        }
        if (this.operator != null) {
            sb.append(this.operator)
        }
        sb.append("?")
        return sb.toString()
    }
}
