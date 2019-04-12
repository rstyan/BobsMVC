package com.clickability.cms.dataaccess.sqlbuilder;

public class IsNull implements Expression {
	
	private final String fieldName;
	
	public IsNull(String fieldName)  {
		this.fieldName = fieldName;
	}

	public String get() {
		return String.format("%s is NULL", this.fieldName);
	}
}
