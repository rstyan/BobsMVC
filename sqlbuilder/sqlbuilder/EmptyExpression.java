package com.clickability.cms.dataaccess.sqlbuilder;

public class EmptyExpression implements Expression {
	
	@Override
	public String get() {
		return "1";
	}

}
