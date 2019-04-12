package com.clickability.cms.dataaccess.sqlbuilder;

public class Resource implements Joinable {

	public final String resource;
	public final String alias;

	public Resource(String resource) {
		this(resource, null);
	}
	
	public Resource(String resource, String alias) {
		this.resource = resource;
		this.alias = alias;
	}
	
	public String alias() {
		return this.alias==null ? resource : alias;
	}
	
	@Override
	public String get() {
		return this.alias == null ? 
			this.resource : 
			String.format("%s as %s", this.resource, this.alias);
	}

}
