package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.StringJoiner;

public class MultiDelete extends Delete {

	private StringJoiner tables;
	
	public MultiDelete() {
		this.tables = new StringJoiner(",");
	}
	
	public MultiDelete add(Resource table) {
		tables.add(table.alias());
		return this;
	}
	
	public MultiDelete where(Expression where) {
		this.where = where;
		return this;
	}
	
	public MultiDelete from(Joinable from) {
		this.from = from;
		return this;
	}
	
	@Override
	public String get() {
		return String.format("delete %s from %s where %s", this.tables.toString(), this.from.get(), this.where.get());
	}

}
