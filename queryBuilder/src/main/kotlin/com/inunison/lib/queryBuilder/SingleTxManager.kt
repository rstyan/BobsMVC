package com.inunison.lib.queryBuilder

import java.sql.SQLException

/*
 * Use for insert/update/delete statements.
 *
 * See Also: QueryManager.java for select statements.
 */
class SingleTxManager(dbName: String) : UpdateManager(dbName), TransactionManager {

    @Throws(SQLException::class)
    override fun begin() {
        this.connection.setAutoCommit(false)
    }

    @Throws(SQLException::class)
    override fun commit() {
        this.connection.commit()
    }

    @Throws(SQLException::class)
    override fun rollback() {
        this.connection.rollback()
    }

    @Throws(SQLException::class)
    override fun end() {
        this.connection.setAutoCommit(true)
        super.close()
    }

    override fun close() {
        // Don't autoclose the connection
        // it is needed for the rest of the transaction.
    }
}
