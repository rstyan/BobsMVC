package com.clickability.cms.dataaccess.sqlbuilder;

import com.clickability.cms.type.DesignType;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * For binding PreparedStatement '?' to actual values.
 * 
 * Add types as needed.
 * 
 */
public class Binding {

	public final BindingType type;
	public final Object value;
	
	// Add more if you need to.  You'll need an enum and a constructor.
	public enum BindingType {
		Array("setArray", Types.ARRAY, char[].class),
		Boolean("setBoolean", Types.TINYINT, boolean.class, Boolean.class),
		Byte("setByte", Types.TINYINT, byte.class, Byte.class),
		Char("setChar", Types.CHAR, char.class, Character.class),
		Short("setShort", Types.SMALLINT, short.class, Short.class),
		Int("setInt", Types.INTEGER, int.class, Integer.class),
		Long("setLong", Types.BIGINT, long.class, Long.class),
		Float("setFloat", Types.REAL, float.class, Float.class),
		Double("setDouble", Types.DOUBLE, double.class, Double.class),
		String("setString", Types.VARCHAR, String.class, DesignType.class),
		Date("setDate", Types.DATE, Date.class, java.util.Date.class),
		Time("setTime", Types.TIME, Time.class),
		TimeStamp("setTimestamp", Types.TIMESTAMP, Timestamp.class);
		
		public final String method;
		public final Class<?>[] classes;
		public final int sqlType;
		BindingType(String method, int type, Class<?>...classes) {
			this.method = method;
			this.classes = classes;
			this.sqlType = type;
		}
	}
	
	public Binding(Object v) {
		this.type = toBindingType(v.getClass());
		this.value = toSqlType(v);
	}
	
	// perform any supported conversions here, like
	// java.util.Date to java.sql.Timestamp
	public static Object toSqlType(Object value) {
		if (value.getClass().equals(java.util.Date.class)) {
			return new Timestamp(((java.util.Date)value).getTime());
		}

		// We support clickability common types too!
		else if (value.getClass().equals(DesignType.class)) {
			return ((DesignType)value).value();
		}
		return value;
	}
	
	// FIXME make sure value class is consistent.
	public static Object fromSqlType(Object value, Class<?> type) {
		if (value == null) return value;
		if (type.equals(DesignType.class)) {
			return DesignType.fromValue((String)value);
		}
		if (type.equals(java.util.Date.class)) {
			return new java.util.Date(((Timestamp)value).getTime());
		}
		return value;
	}
	
	public Binding(Class<?> type) {
		this.type = toBindingType(type);
		this.value = null;
	}
	
	private BindingType toBindingType(Class<?> type) {
		for (BindingType bt : BindingType.values()) {
			for (Class<?> clazz : bt.classes) {
				if (type.equals(clazz)) {
					return bt;
				}
			}
		}
		throw new DataaccessException(String.format("binding type %s not supported", type.getName()));
	}
	
}
