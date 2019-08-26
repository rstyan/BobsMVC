package com.clickability.cms.dataaccess.service

import java.lang.reflect.Method

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.apache.log4j.Logger

import com.clickability.cms.dataaccess.sqlbuilder.TransactionFailureException
import com.clickability.cms.dataaccess.sqlbuilder.TransactionManager
import com.clickability.cms.dataaccess.sqlbuilder.TransactionManagerFactory
import com.clickability.cms.dataaccess.sqlbuilder.annotations.DbTransaction

class DbServiceProxy : MethodInterceptor {

    @Override
    @Throws(Throwable::class)
    operator fun invoke(invocation: MethodInvocation): Object? {
        val m = invocation.getMethod()
        val t = m.getAnnotation(DbTransaction::class.java)
        val tm = TransactionManagerFactory.getTxManager(t.value())
        var result: Object? = null
        try {
            tm.begin()
            result = invocation.proceed()
            tm.commit()
        } catch (e: TransactionFailureException) {
            tm.rollback()
            throw TransactionFailureException(e)
        } catch (e: Throwable) {
            logger.error("transaction failure, rolling back.", e)
            tm.rollback()
            throw TransactionFailureException(e)
        } finally {
            tm.end()
        }// The invoke method is ahem... invoked recursively.  No need for logging here,
        // as it was logged already below in an earlier invocation.
        return result
    }

    companion object {

        private val logger = Logger.getLogger(DbServiceProxy::class.java)
    }
}
