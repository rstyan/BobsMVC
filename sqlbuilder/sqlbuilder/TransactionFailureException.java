package com.clickability.cms.dataaccess.sqlbuilder;

public class TransactionFailureException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TransactionFailureException(Throwable root) {
		super(root);
	}

}
