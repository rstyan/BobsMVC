package com.clickability.cms.dataaccess.sqlbuilder;

public abstract class AbstractCompare<T> implements Expression {
	
	protected String lhs;
	protected String op;
	protected String rhs;
	
	public AbstractCompare() {
		this.lhs = "1";
		this.op = "=";
		this.rhs = "?";
	}
	
	public AbstractCompare<T> using(String op) {
		this.op = op;
		return this;
	}
	
	public AbstractCompare<T> lhs(String operand) {
		this.lhs = operand;
		return this;
	}

	public AbstractCompare<T> lhs(Column operand) {
		return this.lhs(operand.get());
	}
	
	public AbstractCompare<T> rhs(String operand) {
		this.rhs = String.format("'%s'", operand);
		return this;
	}

	private AbstractCompare<T> add(String operand) {
		this.rhs = operand;
		return this;
	}

	public AbstractCompare<T> rhs(Column operand) {
		return this.add(operand.get());
	}
	
	/*
	 * TODO - add more RHS type as needed.
	 */
	public AbstractCompare<T> rhs(Integer operand) {
		return this.add(operand.toString());
	}
	
	public AbstractCompare<T> rhs(Long operand) {
		return this.add(operand.toString());
	}
	
	public AbstractCompare<T> rhs(Double operand) {
		return this.add(operand.toString());
	}
	
	public AbstractCompare<T> rhs(Float operand) {
		return this.add(operand.toString());
	}
	
	public AbstractCompare<T> rhs(Query q) {
		return this.add(q.get());
	}
	
	public String get() {
		return String.format("(%s)%s(%s)", this.lhs,this.op,this.rhs);
	}

}
