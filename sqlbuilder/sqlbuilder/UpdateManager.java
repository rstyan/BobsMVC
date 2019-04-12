package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.clickability.dbmanager.DBManager;

/*
 * Use for insert/update/delete statements.
 * 
 * See Also: QueryManager.java for select statements.
 */
public class UpdateManager extends QueryManager {

	// Use the factory methods
	UpdateManager() {
		super(DBManager.getGlobalManager(), false);
	}
	
	UpdateManager(String dbName) {
		super(DBManager.getDBManager(dbName), false);
	}
	
	public int execute(Update query, Binding...bindings) throws SQLException {
		try (PreparedStatement stmt = this.connection.prepareStatement(query.get())) {
			int k = 1;

			// Bindings for "set column = operand operator ?"
			for (int i=0; i< query.assignments.size(); i++,k++) {
				bind(stmt, query.assignments.get(i).value, i+1);
			}
			
			// Bindings for where clause
			for (int j=0; j<bindings.length; j++) {
				bind(stmt, bindings[j], k+j);
			}

			return stmt.executeUpdate();
		} 
	}
	
	public int execute(Insert query) throws SQLException {
		return execute(query, true);
	}
	
	public int execute(Insert query, boolean isAutogen) throws SQLException {
		int returnGeneratedKeys = isAutogen ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
		try (PreparedStatement stmt = this.connection.prepareStatement(query.get(), returnGeneratedKeys)) {
			for (int i=0; i<query.data.size(); i++) {
				bind(stmt, query.data.get(i).value, i+1);
			}
			stmt.executeUpdate();
			return isAutogen ? dbManager.getLastInsertID(stmt) : 0;
		}
	}
	
	public void execute(Delete q, Binding...bindings) throws SQLException {
		try (PreparedStatement stmt = this.connection.prepareStatement(q.get())) {
			for (int i=0; i<bindings.length; i++) {
				bind(stmt, bindings[i], i+1);
			}
			stmt.executeUpdate();
		} 
	}
	
}
