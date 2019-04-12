package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class Join implements Joinable {
	
	public enum Type {
		INNER("inner join"), OUTER("left outer join"),
		RIGHT_INNER("right inner join"), RIGHT_OUTER("right outer join");
		public final String name;
		private Type(String name) {
			this.name = name;
		}
	}

	private List<JoinExpression> joins;
	private Joinable base;

	public Join(Joinable base) {
		this.base = base;
		this.joins = new ArrayList<JoinExpression>();
	}
	
	public Join join(JoinExpression...expressions) {
		for (JoinExpression expr : expressions) {
			this.joins.add(expr);
		}
		return this;
	}
	
	@Override
	public String get() {
		String join = String.format("(%s)", this.base.get());
		for (JoinExpression expr: this.joins) {
			join += " " + expr.get();
		}
		return join;
	}
	
}
