package com.clickability.cms.dataaccess.sqlbuilder;

/*
 * Sometimes you need nothing at all.
 */
public class NullElement implements Expression {

	@Override
	public String get() {
		return "";
	}

}
