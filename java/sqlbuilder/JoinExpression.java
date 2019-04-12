package com.clickability.cms.dataaccess.sqlbuilder;

import com.clickability.cms.dataaccess.sqlbuilder.Join.Type;

public class JoinExpression implements Expression {

	private Joinable resource;
	private Type type;
	private Expression criteria;
	
	public JoinExpression(Joinable resource) {
		this.resource = resource;
		this.type = Join.Type.INNER;
		this.criteria = null;
	}
	
	public JoinExpression using(Join.Type type) {
		this.type = type;
		return this;
	}

	public JoinExpression on(Expression criteria) {
		this.criteria = criteria;
		return this;
	}

	@Override
	public String get() {
		String base = String.format("%s (%s)", this.type.name, this.resource.get());
		if (this.criteria != null) {
			base += String.format(" on (%s)", this.criteria.get());
		}
		return base;
	}

}
