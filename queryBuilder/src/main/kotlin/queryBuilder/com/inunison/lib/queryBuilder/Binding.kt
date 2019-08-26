package com.clickability.cms.dataaccess.sqlbuilder

import com.clickability.cms.type.DesignType

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types

/**
 * For binding PreparedStatement '?' to actual values.
 *
 * Add types as needed.
 *
 */
class Binding {

    val type: BindingType
    val value: Object?

    // Add more if you need to.  You'll need an enum and a constructor.
    enum class BindingType private constructor(val method: String, val sqlType: Int, vararg classes: Class<*>) {
        Array("setArray", Types.ARRAY, CharArray::class.java),
        Boolean("setBoolean", Types.TINYINT, Boolean::class.javaPrimitiveType, Boolean::class.java),
        Byte("setByte", Types.TINYINT, Byte::class.javaPrimitiveType, Byte::class.java),
        Char("setChar", Types.CHAR, Char::class.javaPrimitiveType, Character::class.java),
        Short("setShort", Types.SMALLINT, Short::class.javaPrimitiveType, Short::class.java),
        Int("setInt", Types.INTEGER, Int::class.javaPrimitiveType, Integer::class.java),
        Long("setLong", Types.BIGINT, Long::class.javaPrimitiveType, Long::class.java),
        Float("setFloat", Types.REAL, Float::class.javaPrimitiveType, Float::class.java),
        Double("setDouble", Types.DOUBLE, Double::class.javaPrimitiveType, Double::class.java),
        String("setString", Types.VARCHAR, String::class.java, DesignType::class.java),
        Date("setDate", Types.DATE, Date::class.java, java.util.Date::class.java),
        Time("setTime", Types.TIME, Time::class.java),
        TimeStamp("setTimestamp", Types.TIMESTAMP, Timestamp::class.java);

        val classes: Array<Class<*>>

        init {
            this.classes = classes
        }
    }

    constructor(v: Object) {
        this.type = toBindingType(v.getClass())
        this.value = toSqlType(v)
    }

    constructor(type: Class<*>) {
        this.type = toBindingType(type)
        this.value = null
    }

    private fun toBindingType(type: Class<*>): BindingType {
        for (bt in BindingType.values()) {
            for (clazz in bt.classes) {
                if (type.equals(clazz)) {
                    return bt
                }
            }
        }
        throw DataaccessException(String.format("binding type %s not supported", type.getName()))
    }

    companion object {

        // perform any supported conversions here, like
        // java.util.Date to java.sql.Timestamp
        fun toSqlType(value: Object): Object {
            if (value.getClass().equals(java.util.Date::class.java)) {
                return Timestamp((value as java.util.Date).getTime())
            } else if (value.getClass().equals(DesignType::class.java)) {
                return (value as DesignType).value()
            }
            return value
        }

        // FIXME make sure value class is consistent.
        fun fromSqlType(value: Object?, type: Class<*>): Object? {
            if (value == null) return value
            if (type.equals(DesignType::class.java)) {
                return DesignType.fromValue(value as String?)
            }
            return if (type.equals(java.util.Date::class.java)) {
                java.util.Date((value as Timestamp).getTime())
            } else value
        }
    }

}
