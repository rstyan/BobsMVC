package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.Collection;
import java.util.StringJoiner;

public class InCollection<T> extends AbstractCompare<Equal> {
	
	private Collection<T> collection;
	
	public InCollection()  {
		super();
		this.using("in");
	}

	public InCollection(String lhs, Collection<T> rhs) {
		this();
		lhs(lhs);
		this.collection = rhs;
	}

	public InCollection(Column lhs, Collection<T> rhs) {
		this();
		lhs(lhs);
		this.collection = rhs;
	}

	@Override
	public String get() {
		return String.format("(%s)%s(%s)", this.lhs, this.op, this.getValues(collection));
	}
	
	private String getValues(Collection<T> collection) {
		StringJoiner sb = new StringJoiner(",");
		for (T value : collection) {
			sb.add(value.toString());
		}
		return sb.toString();
	}

}
