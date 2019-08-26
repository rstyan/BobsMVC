package com.inunison.lib.queryBuilder

import java.sql.SQLException

/*
 * Do nothing.  Its a transaction manager to handle nested
 * transactions, which should not start or end a transaction,
 * they are simply part of an existing one.
 */
internal open class PlaceboTxManager : TransactionManager {

    @Throws(SQLException::class)
    override fun begin() {
    }

    @Throws(SQLException::class)
    override fun commit() {
    }

    @Throws(SQLException::class)
    override fun rollback() {
    }

    override fun end() {
    }

}
