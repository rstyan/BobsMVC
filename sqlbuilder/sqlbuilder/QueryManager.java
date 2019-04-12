package com.clickability.cms.dataaccess.sqlbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.clickability.dbmanager.DBManager;

/*
 * This is for read only queries.  
 * 
 * See Also: UpdateManager.java for insert/update/delete type queries.
 */
public class QueryManager implements AutoCloseable {
	
	protected Connection connection;
	protected DBManager dbManager;
	
	public QueryManager(DBManager dbManager) {
		this(dbManager, true);
	}
	
	protected QueryManager(DBManager dbManager, boolean readOnly) {
		this.dbManager = dbManager;
		this.connection = dbManager.getConnection(readOnly);
	}
	
	public DBManager getDbManager() {
		return dbManager;
	}
	
	public <T> List<T> execute(Query q, Function<ResultSet, T> populator) throws SQLException {
		List<Binding> bindings = new ArrayList<>();
		return execute(q, bindings, populator);
	}

	public <T> List<T> execute(Query q, Binding binding, Function<ResultSet, T> populator) throws SQLException {
		List<Binding> bindings = new ArrayList<>();
		bindings.add(binding);
		return execute(q, bindings, populator);
	}

	public <T> List<T> execute(Query q, Binding[] bindings, Function<ResultSet, T> populator) throws SQLException {
		List<T> results = new ArrayList<>();
		try (PreparedStatement stmt = this.connection.prepareStatement(q.get())) {
			for (int i=0; i<bindings.length; i++) {
				bind(stmt, bindings[i], i+1);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					results.add(populator.apply(rs));
				}
			} 
		} 
		return results;
	}
	
	public <T> List<T> execute(Query q, List<Binding> bindings, Function<ResultSet, T> populator) throws SQLException {
		Binding[] bList = bindings.toArray(new Binding[bindings.size()]);
		return execute(q, bList, populator);
	}
	
	protected void bind(PreparedStatement stmt, Binding binding, int index) throws SQLException {
		if (binding.value == null) {
			stmt.setNull(index, binding.type.sqlType);
		}
		else {
			stmt.setObject(index, binding.value);
		}
	}

	@Override
	public void close() {
		this.dbManager.release(this.connection);
		this.connection = null;
	}
	
}
