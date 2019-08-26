package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.SQLException

interface TransactionManager {

    @Throws(SQLException::class)
    fun begin()

    @Throws(SQLException::class)
    fun commit()

    @Throws(SQLException::class)
    fun rollback()

    @Throws(SQLException::class)
    fun end()

}
