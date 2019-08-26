package com.inunison.lib.queryBuilder.annotations

import com.clickability.dbmanager.DBManager
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
annotation class Table(val value: String, val database: String = DBManager.DB_CMS)
