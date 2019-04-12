package com.clickability.cms.dataaccess.sqlbuilder;

public class Column implements Expression {
	
	public final String name;
	public final Resource resource;
	
	public Column(String name) {
		this.name = name;
		this.resource = null;
	}

	public Column(Resource resource, String name) {
		this.name = name;
		this.resource = resource;
	}

	@Override
	public String get() {
		return this.resource == null ?
			this.name : 
			String.format("%s.%s", this.resource.alias(), this.name);
	}

}
