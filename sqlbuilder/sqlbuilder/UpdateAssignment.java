package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.Date;

public class UpdateAssignment implements Expression {
	
	public final String operand;
	public final Binding value;
	public final String operator;
	public final String column;
	
	private UpdateAssignment(String column, String operand, String operator, Binding value) {
		this.operand = operand;
		this.value = value;
		this.operator = operator;
		this.column = column;
	}
	
	public UpdateAssignment(String column, Binding value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, Boolean value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Boolean value) {
		this(column, operand, operator, value == null ? new Binding(Boolean.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Byte value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Byte value) {
		this(column, operand, operator, value == null ? new Binding(Byte.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Character value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Character value) {
		this(column, operand, operator, value == null ? new Binding(Character.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Short value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Short value) {
		this(column, operand, operator, value == null ? new Binding(Short.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Integer value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Integer value) {
		this(column, operand, operator, value == null ? new Binding(Integer.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Long value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Long value) {
		this(column, operand, operator, value == null ? new Binding(Long.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Float value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Float value) {
		this(column, operand, operator, value == null ? new Binding(Float.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Double value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Double value) {
		this(column, operand, operator, value == null ? new Binding(Double.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, String value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, String value) {
		this(column, operand, operator, value == null ? new Binding(String.class) : new Binding(value));
	}
	
	public UpdateAssignment(String column, Date value) {
		this(column, null, null, value);
	}
	
	public UpdateAssignment(String column, String operand, String operator, Date value) {
		this(column, operand, operator, value == null ? new Binding(Date.class) : new Binding(value));
	}

	@Override
	public String get() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.column);
		sb.append("=");
		if (this.operand != null) {
			sb.append(this.operand);
		}
		if (this.operator != null) {
			sb.append(this.operator);
		}
		sb.append("?");
		return sb.toString();
	}
}
