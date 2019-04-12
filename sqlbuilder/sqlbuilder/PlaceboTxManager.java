package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.SQLException;

/*
 * Do nothing.  Its a transaction manager to handle nested
 * transactions, which should not start or end a transaction,
 * they are simply part of an existing one.
 */
class PlaceboTxManager implements TransactionManager {

	@Override
	public void begin() throws SQLException {
	}
	
	@Override
	public void commit() throws SQLException {
	}

	@Override
	public void rollback() throws SQLException {
	}
	
	@Override
	public void end() {
	}

}
