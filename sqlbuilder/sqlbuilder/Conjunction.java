package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class Conjunction implements Expression {

	private List<Expression> predicates;
	private Expression first;

	public Conjunction(Expression first) {
		this.predicates = new ArrayList<Expression>();
		this.first = first;
	}

	public Conjunction and(Expression... clauses) {
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
			result += String.format(" and (%s)", predicate.get());
		}
		return result;
	}

}
