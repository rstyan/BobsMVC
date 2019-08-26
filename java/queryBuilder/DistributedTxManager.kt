package com.clickability.cms.dataaccess.sqlbuilder

import java.sql.SQLException
import java.util.HashMap

import org.apache.log4j.Logger

import com.clickability.dbmanager.DBManager

/**
 * A transaction manager for distributed databases.  The design is
 * as follows:
 *
 * 1) There is a single, primary transaction that kicks the whole thing
 * off.
 *
 * 2) Within the primary transaction, secondary transactions may be encountered
 * on different databases.
 *
 * 3) All operations on the primary database are under the control of a single
 * transaction manager.
 *
 * 4) Operations on secondary databases are under the control of separate transaction
 * managers.
 *
 * 5) All commit/rollback operations on the single transaction managers are deferred
 * until the primary transaction has completed.
 *
 * 6) If any operation failed, on any single transaction manager, all operations
 * fail.  Failures are signaled through Exceptions.
 *
 * 7) As secondary transactions complete the connections are idle, awaiting the completion
 * of the primary transaction before being committed.
 *
 * @author roystyan
 */
class DistributedTxManager(primaryManager: SingleTxManager) : TransactionManager {

    private val activeManagers: Map<DBManager, SingleTxManager>
    private val primaryManager: ProxyTxManager

    init {
        activeManagers = HashMap()
        this.primaryManager = ProxyTxManager(primaryManager)
        activeManagers.put(primaryManager.getDbManager(), primaryManager)
    }

    fun activate(txm: SingleTxManager): ProxyTxManager {
        activeManagers.put(txm.getDbManager(), txm)
        return ProxyTxManager(txm)
    }

    fun getUpdateManager(dbName: String): UpdateManager {
        return activeManagers[DBManager.getDBManager(dbName)]
    }

    fun isInTransaction(dbName: String): Boolean {
        return activeManagers[DBManager.getDBManager(dbName)] != null
    }

    @Override
    @Throws(SQLException::class)
    fun begin() {
        primaryManager.begin()
    }

    /*
	 * The choice here is that if one commit fails any remaining commits
	 * will be rolled back.  If any previous commits occurred then there
	 * is an out-of-synch condition in our databases.  Sucks to be us.
	 */
    @Override
    @Throws(SQLException::class)
    fun commit() {
        var keepCommitting = true
        var commitException: SQLException? = null
        for (txm in activeManagers.values()) {
            try {
                if (keepCommitting) {
                    txm.commit()
                } else {
                    txm.rollback()
                }
            } catch (e: SQLException) {
                // Uh-oh, this is a really bad place to fail.
                logger.error("Commit transaction failure", e)
                keepCommitting = false
                commitException = e
            }

        }
        if (commitException != null) {
            throw commitException
        }
    }

    @Override
    @Throws(SQLException::class)
    fun rollback() {
        var rollbackException: SQLException? = null
        for (txm in activeManagers.values()) {
            try {
                txm.rollback()
            } catch (e: SQLException) {
                // log it, but keep on rollin'
                logger.error("Rollback transaction failure", e)
                rollbackException = e
            }

        }
        if (rollbackException != null) {
            throw rollbackException
        }
    }

    @Override
    fun end() {
        try {
            for (txm in activeManagers.values()) {
                try {
                    txm.end()
                } catch (sqle: SQLException) {
                    logger.error("Error closing SQL connection", sqle)
                }

            }
        } finally {
            TransactionManagerFactory.cleanup()
        }
    }

    companion object {

        private val logger = Logger.getLogger(DistributedTxManager::class.java)
    }

}
