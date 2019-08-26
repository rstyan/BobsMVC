package com.clickability.cms.dataaccess.sqlbuilder.annotations

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class DbField(// The name of the database field that maps to the class field.
        val value: String, // Is it a primary key?
        val primary: Boolean = false, // Is the field updateable?
        // Note: primary keys are NOT updatable and this flag is ignored.
        val mutable: Boolean = true, // Is the field auto-populated by something?
        val autogen: Boolean = false, // Is the field a foreign key?
        // NOTE as of Nov 2017 this is only used for documentation.
        val foreign: Boolean = false, /*
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
        val mapsto: Class<*> = Unit::class)
