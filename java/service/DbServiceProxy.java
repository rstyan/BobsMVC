package com.clickability.cms.dataaccess.service;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import com.clickability.cms.dataaccess.sqlbuilder.TransactionFailureException;
import com.clickability.cms.dataaccess.sqlbuilder.TransactionManager;
import com.clickability.cms.dataaccess.sqlbuilder.TransactionManagerFactory;
import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbTransaction;

public class DbServiceProxy implements MethodInterceptor {
	
	private static Logger logger = Logger.getLogger(DbServiceProxy.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method m = invocation.getMethod();
		DbTransaction t = m.getAnnotation(DbTransaction.class);
		TransactionManager tm = TransactionManagerFactory.getTxManager(t.value());
		Object result = null;
		try {
			tm.begin();
			result = invocation.proceed();
			tm.commit();
		}
		// The invoke method is ahem... invoked recursively.  No need for logging here, 
		// as it was logged already below in an earlier invocation.
		catch (TransactionFailureException e) {
			tm.rollback();
			throw new TransactionFailureException(e);
		}
		catch (Throwable e) {
			logger.error("transaction failure, rolling back.", e);
			tm.rollback();
			throw new TransactionFailureException(e);
		}
		finally {
			tm.end();				
		}
		return result;
	}
}
