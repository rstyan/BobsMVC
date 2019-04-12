package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.clickability.dbmanager.DBManager;

/**
 * A transaction manager for distributed databases.  The design is
 * as follows:
 * 
 * 1) There is a single, primary transaction that kicks the whole thing
 *    off.
 *    
 * 2) Within the primary transaction, secondary transactions may be encountered
 *    on different databases.
 *    
 * 3) All operations on the primary database are under the control of a single 
 *    transaction manager.
 *    
 * 4) Operations on secondary databases are under the control of separate transaction
 *    managers.
 *    
 * 5) All commit/rollback operations on the single transaction managers are deferred
 *    until the primary transaction has completed.
 *    
 * 6) If any operation failed, on any single transaction manager, all operations
 *    fail.  Failures are signaled through Exceptions.
 *    
 * 7) As secondary transactions complete the connections are idle, awaiting the completion
 *    of the primary transaction before being committed.
 * 
 * @author roystyan
 *
 */
public class DistributedTxManager implements TransactionManager {
	
	private static Logger logger = Logger.getLogger(DistributedTxManager.class);
	
	private final Map<DBManager, SingleTxManager> activeManagers;
	private final ProxyTxManager primaryManager;
	
	public DistributedTxManager(SingleTxManager primaryManager) {
		activeManagers = new HashMap<>();
		this.primaryManager = new ProxyTxManager(primaryManager);
		activeManagers.put(primaryManager.getDbManager(), primaryManager);
	}
	
	public ProxyTxManager activate(SingleTxManager txm) {
		activeManagers.put(txm.getDbManager(), txm);
		return new ProxyTxManager(txm);
	}
	
	public UpdateManager getUpdateManager(String dbName) {
		return activeManagers.get(DBManager.getDBManager(dbName));
	}
	
	public boolean isInTransaction(String dbName) {
		return activeManagers.get(DBManager.getDBManager(dbName)) != null;
	}
	
	@Override
	public void begin() throws SQLException {
		primaryManager.begin();
	}
	
	/*
	 * The choice here is that if one commit fails any remaining commits
	 * will be rolled back.  If any previous commits occurred then there
	 * is an out-of-synch condition in our databases.  Sucks to be us.
	 */
	@Override
	public void commit() throws SQLException {
		boolean keepCommitting = true;
		SQLException commitException = null;
		for (SingleTxManager txm : activeManagers.values()) {
			try {
				if (keepCommitting) {
					txm.commit();
				}
				else {
					txm.rollback();
				}
			} 
			catch (SQLException e) {
				// Uh-oh, this is a really bad place to fail.
				logger.error("Commit transaction failure", e);
				keepCommitting = false;
				commitException = e;
			}
		}
		if (commitException != null) {
			throw commitException;
		}
	}

	@Override
	public void rollback() throws SQLException {
		SQLException rollbackException = null;
		for (SingleTxManager txm : activeManagers.values()) {
			try {
				txm.rollback();
			} 
			catch (SQLException e) {
				// log it, but keep on rollin'
				logger.error("Rollback transaction failure", e);
				rollbackException = e;
			}
		}
		if (rollbackException != null) {
			throw rollbackException;
		}
	}

	@Override
	public void end() {
		try {
			for (SingleTxManager txm : activeManagers.values()) {
				try {
					txm.end();
				}
				catch (SQLException sqle) {
					logger.error("Error closing SQL connection", sqle);
				}
			}
		}
		finally {
			TransactionManagerFactory.cleanup();
		}
	}

}
