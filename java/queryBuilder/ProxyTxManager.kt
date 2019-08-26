package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.SQLException

/**
 * Proxy Transaction Manager:  a wrapper class used in conjunction
 * with the single and distributed transaction managers.  It is used
 * to defer certain single transaction operations to the distributed
 * manager, such as commit, rollback, and end.  In the end, all it
 * really needs to do is start the transaction.
 *
 * @author roystyan
 */
class ProxyTxManager(val actualManager: SingleTxManager) : PlaceboTxManager() {

    @Override
    @Throws(SQLException::class)
    fun begin() {
        actualManager.begin()
    }

}
