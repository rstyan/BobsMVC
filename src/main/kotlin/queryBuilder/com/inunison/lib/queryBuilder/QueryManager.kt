package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import java.util.function.Function

import com.clickability.dbmanager.DBManager

/*
 * This is for read only queries.
 *
 * See Also: UpdateManager.java for insert/update/delete type queries.
 */
class QueryManager protected constructor(dbManager: DBManager, readOnly: Boolean) : AutoCloseable {

    protected var connection: Connection? = null
    var dbManager: DBManager
        protected set

    constructor(dbManager: DBManager) : this(dbManager, true) {}

    init {
        this.dbManager = dbManager
        this.connection = dbManager.getConnection(readOnly)
    }

    @Throws(SQLException::class)
    fun <T> execute(q: Query, populator: Function<ResultSet, T>): List<T> {
        val bindings = ArrayList()
        return execute(q, bindings, populator)
    }

    @Throws(SQLException::class)
    fun <T> execute(q: Query, binding: Binding, populator: Function<ResultSet, T>): List<T> {
        val bindings = ArrayList()
        bindings.add(binding)
        return execute(q, bindings, populator)
    }

    @Throws(SQLException::class)
    fun <T> execute(q: Query, bindings: Array<Binding>, populator: Function<ResultSet, T>): List<T> {
        val results = ArrayList()
        this.connection!!.prepareStatement(q.get()).use({ stmt ->
            for (i in bindings.indices) {
                bind(stmt, bindings[i], i + 1)
            }
            stmt.executeQuery().use({ rs ->
                while (rs.next()) {
                    results.add(populator.apply(rs))
                }
            })
        })
        return results
    }

    @Throws(SQLException::class)
    fun <T> execute(q: Query, bindings: List<Binding>, populator: Function<ResultSet, T>): List<T> {
        val bList = bindings.toArray(arrayOfNulls<Binding>(bindings.size()))
        return execute(q, bList, populator)
    }

    @Throws(SQLException::class)
    protected fun bind(stmt: PreparedStatement, binding: Binding, index: Int) {
        if (binding.value == null) {
            stmt.setNull(index, binding.type.sqlType)
        } else {
            stmt.setObject(index, binding.value)
        }
    }

    @Override
    fun close() {
        this.dbManager.release(this.connection)
        this.connection = null
    }

}
