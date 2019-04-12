package com.clickability.cms.dataaccess.sqlbuilder;

public class Compare extends AbstractCompare<Compare> {
	
	public Compare() {
		super();
	}
	
	public Compare(String lhs, String op) {
		this();
		lhs(lhs);
		using(op);
	}
	
	public Compare(String lhs, String op, String rhs) {
		this(lhs, op);
		rhs(rhs);
	}

}
