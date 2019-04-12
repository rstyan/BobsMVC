package com.clickability.cms.dataaccess.sqlbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbField {
	
	// The name of the database field that maps to the class field.
	String value();
	
	// Is it a primary key?
	boolean primary() default false;
	
	// Is the field updateable?
	// Note: primary keys are NOT updatable and this flag is ignored.
	boolean mutable() default true;
	
	// Is the field auto-populated by something?
	boolean autogen() default false;
	
	// Is the field a foreign key?
	// NOTE as of Nov 2017 this is only used for documentation.
	boolean foreign() default false;
	
	/*
	 * A bit of a hack:  If the data type in the Db is different
	 * from the one in the class, this attribute tells the
	 * system to look for mapping methods when accessing
	 * data.  For example, if you class field is type Colour bob 
	 * where Colour is an enum and your database field is integer, 
	 * you would set mapsto=int and provide:
	 *		void toBob(int value);
	 *		int fromBob();
	 * or use the standard getter/setter convention.
	 *      int getBob();
	 *      void setBob(int value);
	 * These "getter/setters" can be private.
	 */
	Class<?> mapsto() default void.class;
}
