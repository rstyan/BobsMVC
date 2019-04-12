package com.clickability.cms.dataaccess.sqlbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * One to many reference to an object.
 * At this point the foreign key is assumed to be an integer
 * TODO relax that restriction
 *
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {

	// The name of the foreign key in the referencing object
	String foreignKey();
	
	// The name of the referenced object's primary key.
	String referencedField();
}
