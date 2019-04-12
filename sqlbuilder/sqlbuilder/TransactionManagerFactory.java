package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.SQLException;

/**
 *  The class serves a dual purpose:
 *  
 *  1) to return an "Update Manager" on request, suitable for the current transaction
 *     context which may, in fact, be none.  Update Managers handle insert, update,
 *     and delete request on the specified database.
 *     
 *  2) to return a Transaction Manager suitable for a new Transaction.  There are three
 *     possibilities:
 *     
 *     1) This is the start of a new transaction.  All new transactions are handled by
 *        the distributed transaction manager.
 *        
 *     2) This is a nested transaction, using the same database as the current
 *        transaction.  A placebo is returned, as it will leave all handling
 *        to the outer transaction.
 *        
 *     3) This is a nested transaction, but a request has come for a transaction on
 *        another database.  A new, single transaction manager is created, handed
 *        over to the outer distributed transaction manager, and returned for further
 *        processing.
 *        
 */
public class TransactionManagerFactory {
	
	private static final ThreadLocal<DistributedTxManager> txManager = new ThreadLocal<DistributedTxManager>();
	
	public static UpdateManager get(String dbName) throws SQLException {
		// if in a transaction
		if (txManager.get() != null) {
			// If the transaction is continuing on a new database
			// initialize a new transaction manager.
			if (!txManager.get().isInTransaction(dbName)) {
				SingleTxManager realTxm = new SingleTxManager(dbName);
				// Do this here, since this manager is not going through DbServiceProxy
				realTxm.begin();
				txManager.get().activate(realTxm);
			}
			return txManager.get().getUpdateManager(dbName);
		}
		return new UpdateManager(dbName);
	}
	
	public static TransactionManager getTxManager(String dbName) {
		// The distributed manager should go out exactly once,
		// at the start of a brand new transaction
		if (txManager.get() == null) {
			SingleTxManager realTxm = new SingleTxManager(dbName);
			txManager.set(new DistributedTxManager(realTxm));
			return txManager.get();
		}
		
		// Request for a transaction on a new database within an existing transaction.
		else if (!txManager.get().isInTransaction(dbName)) {
			SingleTxManager realTxm = new SingleTxManager(dbName);
			return txManager.get().activate(realTxm);
		}
		
		// We're in an existing transaction; the manager does nothing.
		return new PlaceboTxManager();
	}
	
	public static void cleanup() {
		txManager.set(null);
	}
}
