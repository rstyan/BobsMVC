package com.inunison.lib.queryBuilder.annotations

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

import com.clickability.dbmanager.DBManager

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation
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
class DbTransaction(// Value = name of the DBManager database in which the transaction takes place.
        val value: String = DBManager.DB_CMS)
