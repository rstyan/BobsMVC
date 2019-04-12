package com.clickability.cms.dataaccess.sqlbuilder;

public class Delete implements Statement {

	protected Joinable from;
	protected Expression where;
	
	public Delete(Resource from) {
		this();
		this.from = from;
	}
	
	protected Delete() {
		this.where = new EmptyExpression();
	}
	
	/**
	 * Returns a conjunction of the input criteria
	 * For disjunctions use where(new Disjunction(....).or(...))
	 */
	public Delete where(Expression ... criteria) {
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

	@Override
	public String get() {
		return String.format("delete from %s where %s", this.from.get(), this.where.get());
	}

}
