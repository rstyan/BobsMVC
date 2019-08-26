package com.inunison.lib.queryBuilder.annotations

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * One to many reference to an object.
 * At this point the foreign key is assumed to be an integer
 * TODO relax that restriction
 *
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class Reference(// The name of the foreign key in the referencing object
        val foreignKey: String, // The name of the referenced object's primary key.
        val referencedField: String)
