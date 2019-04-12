package com.clickability.cms.dataaccess.sqlbuilder;

public class Equal extends AbstractCompare<Equal> {

	public Equal() {
		super();
		this.using("=");
	}

	public Equal(Column lhs, Column rhs) {
		this();
		lhs(lhs);
		rhs(rhs);
	}

	public Equal(Column lhs) {
		this();
		lhs(lhs);
	}

	public Equal(String lhs) {
		this();
		lhs(lhs);
	}

	public Equal(String lhs, String rhs) {
		this();
		lhs(lhs);
		rhs(rhs);
	}

	public Equal(String lhs, int rhs) {
		this();
		lhs(lhs);
		rhs(rhs);
	}
}
