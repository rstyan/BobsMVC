package com.clickability.cms.dataaccess.sqlbuilder;

import java.util.StringJoiner;

public class OrderBy implements Expression {
	
	public static enum Direction { 
		ASC("asc"), DESC("desc");
		public final String name;
		private Direction(String name) {
			this.name= name;
		}
	};

	private StringJoiner orderby;
	
	public OrderBy() {
		this.orderby = new StringJoiner(",");
	}
	
	public OrderBy(String column) {
		this();
		this.add(column);
	}
	
	public OrderBy(String column, Direction dir) {
		this();
		this.add(column, dir);
	}
	
	public OrderBy(Column column) {
		this();
		this.add(column);
	}
	
	public OrderBy(Column column, Direction dir) {
		this();
		this.add(column, dir);
	}
	
	public OrderBy add(String column) {
		this.add(new Column(column), Direction.ASC);
		return this;
	}
	
	public OrderBy add(String column, Direction dir) {
		this.add(new Column(column), dir);
		return this;
	}
	
	public OrderBy add(Column column) {
		this.add(column, Direction.ASC);
		return this;
	}
	
	public OrderBy add(Column column, Direction direction) {
		this.orderby.add(column.get() + " " + direction.name);
		return this;
	}

	@Override
	public String get() {
		return String.format("order by %s", this.orderby.toString());
	}

}
