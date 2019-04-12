package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.StringJoiner;

public class GroupBy implements Expression {

	private StringJoiner groupBy;
	
	public GroupBy() {
		this.groupBy = new StringJoiner(",");
	}
	
	public GroupBy(Column column) {
		this();
		add(column);
	}
	
	public GroupBy(String column) {
		this();
		add(column);
	}
	
	public GroupBy add(Column column) {
		this.groupBy.add(column.get());
		return this;
	}
	
	public GroupBy add(String column) {
		this.groupBy.add(column);
		return this;
	}
	
	@Override
	public String get() {
		return String.format("group by %s", this.groupBy.toString());
	}


}
