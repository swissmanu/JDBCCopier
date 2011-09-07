package me.alabor.jdbccopier.database.meta;

/**
 * Field types for the {@link Field} class.
 * 
 * @author Manuel Alabor
 * @see http://www.techonthenet.com/sql/datatypes.php
 */
public enum FieldType {
	Integer,
	BigInt,  // added
	Smallint,
	Numeric,
	Decimal,
	Real,
	DoublePrecision,
	Float,
	Character,
	CharacterVarying,
	Bit,
	BitVarying,
	Date,
	Time,
	Timestamp,
	TimeWithTimeZone,
	TimestampWithTimeZone,
	YearMonthInterval,
	DayTimeInterval
}
