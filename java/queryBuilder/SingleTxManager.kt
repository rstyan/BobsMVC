package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.SQLException

/*
 * Use for insert/update/delete statements.
 *
 * See Also: QueryManager.java for select statements.
 */
class SingleTxManager(dbName: String) : UpdateManager(dbName), TransactionManager {

    @Throws(SQLException::class)
    fun begin() {
        this.connection.setAutoCommit(false)
    }

    @Throws(SQLException::class)
    fun commit() {
        this.connection.commit()
    }

    @Throws(SQLException::class)
    fun rollback() {
        this.connection.rollback()
    }

    @Throws(SQLException::class)
    fun end() {
        this.connection.setAutoCommit(true)
        super.close()
    }

    @Override
    fun close() {
        // Don't autoclose the connection
        // it is needed for the rest of the transaction.
    }

}
