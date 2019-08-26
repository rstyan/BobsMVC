package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.SQLException

/*
 * Do nothing.  Its a transaction manager to handle nested
 * transactions, which should not start or end a transaction,
 * they are simply part of an existing one.
 */
internal class PlaceboTxManager : TransactionManager {

    @Override
    @Throws(SQLException::class)
    fun begin() {
    }

    @Override
    @Throws(SQLException::class)
    fun commit() {
    }

    @Override
    @Throws(SQLException::class)
    fun rollback() {
    }

    @Override
    fun end() {
    }

}
