package com.clickability.cms.dataaccess.sqlbuilder;

public class Query implements Statement {

	protected Selection select;
	protected Joinable from;
	protected Expression where;
	protected GroupBy groupBy;
	protected OrderBy orderBy;
	protected boolean forUpdate;
	
	public Query() {
		this.select = new Selection().add("*");
		// Is there a better default for this?
		this.from = null;
		this.where = new EmptyExpression();
		this.groupBy = null;
		this.orderBy = null;
		this.forUpdate = false;
	}
	
	public Query select(Selection selection) {
		this.select = selection;
		return this;
	}
	
	public Query from(Joinable from) {
		this.from = from;
		return this;
	}

	/**
	 * Returns a conjunction of the input criteria
	 * For disjunctions use where(new Disjunction(....).or(...))
	 */
	public Query where(Expression ... criteria) {
		if (criteria.length < 1) {
			this.where = new EmptyExpression();
		}
		else if (criteria.length == 1) {
			this.where = criteria[0];
		}
		else {
			Conjunction conjunction = new Conjunction(criteria[0]);
			for (int i=1; i<criteria.length; i++) {
				conjunction.and(criteria[i]);
			}
			this.where = conjunction;
		}
		return this;
	}
	
	public Query orderBy(OrderBy order) {
		this.orderBy = order;
		return this;
	}

	public Query groupBy(GroupBy group) {
		this.groupBy = group;
		return this;
	}
	
	public Query forUpdate() {
		this.forUpdate = true;
		return this;
	}

	@Override
	public String get() {
		String base = String.format("select %s from %s where %s", this.select.get(), this.from.get(), this.where.get());
		if (this.groupBy != null) {
			base = String.format("%s %s", base, this.groupBy.get());
		}
		if (this.orderBy != null) {
			base = String.format("%s %s", base, this.orderBy.get());
		}
		if (this.forUpdate) {
			base = String.format("%s %s", base, "FOR UPDATE");
		}
		return base;
	}
	
}
