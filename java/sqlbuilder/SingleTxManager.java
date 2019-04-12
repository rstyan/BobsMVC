package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.SQLException;

/*
 * Use for insert/update/delete statements.
 * 
 * See Also: QueryManager.java for select statements.
 */
public class SingleTxManager extends UpdateManager implements TransactionManager {
	
	public SingleTxManager(String dbName) {
		super(dbName);
	}
	
	public void begin() throws SQLException {
		this.connection.setAutoCommit(false);
	}
	
	public void commit() throws SQLException {
		this.connection.commit();
	}

	public void rollback() throws SQLException {
		this.connection.rollback();
	}
	
	public void end() throws SQLException {
		this.connection.setAutoCommit(true);
		super.close();
	}
	
	@Override
	public void close() {
		// Don't autoclose the connection
		// it is needed for the rest of the transaction.
	}

}
