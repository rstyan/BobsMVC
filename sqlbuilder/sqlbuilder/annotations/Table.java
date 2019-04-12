package com.clickability.cms.dataaccess.sqlbuilder.annotations;

import java.lang.annotation.Target;

import com.clickability.dbmanager.DBManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String value();
	String database() default DBManager.DB_CMS;
}
