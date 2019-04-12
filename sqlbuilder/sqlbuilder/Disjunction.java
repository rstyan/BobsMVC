package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class Disjunction implements Expression {

	private List<Expression> predicates;
	private Expression first;

	public Disjunction(Expression first) {
		this.predicates = new ArrayList<Expression>();
		this.first = first;
	}

	public Disjunction or(Expression... clauses) {
		for (Expression c: clauses) {
			this.predicates.add(c);
		} 
		return this;
	}
	
	public Integer size() {
		return this.predicates.size() + 1;
	}
	
	@Override
	public String get() {
		String result = first.get();
		for (Expression predicate: this.predicates) {
			result += String.format(" or (%s)", predicate.get());
		}
		return result;
	}

}
