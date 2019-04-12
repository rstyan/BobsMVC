package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.SQLException;

public interface TransactionManager {
	
	public void begin() throws SQLException;
	
	public void commit() throws SQLException;

	public void rollback() throws SQLException;

	public void end() throws SQLException;
	
}
