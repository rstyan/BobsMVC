package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

import com.clickability.dbmanager.DBManager

/*
 * Use for insert/update/delete statements.
 *
 * See Also: QueryManager.java for select statements.
 */
class UpdateManager : QueryManager {

    // Use the factory methods
    internal constructor() : super(DBManager.getGlobalManager(), false) {}

    internal constructor(dbName: String) : super(DBManager.getDBManager(dbName), false) {}

    @Throws(SQLException::class)
    fun execute(query: Update, vararg bindings: Binding): Int {
        this.connection.prepareStatement(query.get()).use({ stmt ->
            var k = 1

            // Bindings for "set column = operand operator ?"
            var i = 0
            while (i < query.assignments.size()) {
                bind(stmt, query.assignments.get(i).value, i + 1)
                i++
                k++
            }

            // Bindings for where clause
            for (j in bindings.indices) {
                bind(stmt, bindings[j], k + j)
            }

            return stmt.executeUpdate()
        })
    }

    @Throws(SQLException::class)
    @JvmOverloads
    fun execute(query: Insert, isAutogen: Boolean = true): Int {
        val returnGeneratedKeys = if (isAutogen) Statement.RETURN_GENERATED_KEYS else Statement.NO_GENERATED_KEYS
        this.connection.prepareStatement(query.get(), returnGeneratedKeys).use({ stmt ->
            for (i in 0 until query.data.size()) {
                bind(stmt, query.data.get(i).value, i + 1)
            }
            stmt.executeUpdate()
            return if (isAutogen) dbManager.getLastInsertID(stmt) else 0
        })
    }

    @Throws(SQLException::class)
    fun execute(q: Delete, vararg bindings: Binding) {
        this.connection.prepareStatement(q.get()).use({ stmt ->
            for (i in bindings.indices) {
                bind(stmt, bindings[i], i + 1)
            }
            stmt.executeUpdate()
        })
    }

}
