package com.clickability.cms.dataaccess.sqlbuilder;

/**
 * Same as SQLException except that it is Runtime so that
 * it can be used in lambda expressions.
 * 
 * @author roystyan
 *
 */
public class DataaccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public DataaccessException(Exception cause) {
		super(cause);
	}
	
	public DataaccessException(String message) {
		super(message);
	}
}
