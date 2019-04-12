package com.clickability.cms.dataaccess.sqlbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.clickability.dbmanager.DBManager;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/**
 * Declares a method to be a transaction on the specified database.
 * Used in conjunction with interface DbTransaction. 
 * 
 * WARNING
 * Classes that have transaction methods MUST implement the DbTransaction interface.
 * There are no methods to implement. DbTransacton simply tells Guice
 * to do its thing (inject DbServiceProxy) on methods in the implementing class.
 * This is a constraint for performance reasons.  If it gets to be too painful,
 * i.e. many hours debugging "why doesn't my transaction work" then perhaps we
 * should reconsider it. 
 * 
 */
public @interface DbTransaction {
	// Value = name of the DBManager database in which the transaction takes place.
	String value() default DBManager.DB_CMS;
}
