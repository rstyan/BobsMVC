package com.clickability.cms.dataaccess.sqlbuilder;

public class Limit implements Expression {
	
	private Integer offset;
	private Integer limit;
	
	public Limit() {
		this.offset = 0;
		this.limit = 1;
	}
	
	public Limit(Integer limit) {
		this.offset = 0;
		this.limit = limit;
	}
	
	public Limit offset(Integer offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public String get() {
		return String.format("limit %s, %s", this.offset, this.limit);
	}

}
