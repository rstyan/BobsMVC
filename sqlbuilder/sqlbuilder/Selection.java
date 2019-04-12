package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.StringJoiner;

public class Selection implements Expression {

	private StringJoiner selectList;
	private Resource base;
	private int size;
	
	public Selection() {
		this.selectList = new StringJoiner(",");
		this.size = 0;
	}
	
	public Selection(Resource base) {
		this();
		this.base = base;
	}
	
	public Selection(String ...columns) {
		this();
		for (String column : columns) {
			this.append(column);
		}
	}
	
	public Selection(Column ...columns) {
		this();
		for (Column column : columns) {
			this.append(column.get());
		}
	}
	
	public int size() {
		return this.size;
	}
	
	protected void append(String item) {
		this.size += 1;
		this.selectList.add(item);
	}
	
	public Selection add(String...columns) {
		for (String column : columns) {
			this.append(column);
		}
		return this;
	}

	public Selection add(Column...columns) {
		for (Column column : columns) {
			this.append(column.get());
		}
		return this;
	}

	@Override
	public String get() {
		String selection = "";
		if (this.base != null) {
			selection += this.base.alias();
		}
		if (this.size > 0) {
			selection += this.selectList.toString();
		}
		return  selection;
	}

}
