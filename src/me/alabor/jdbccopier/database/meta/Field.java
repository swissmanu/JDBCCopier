package me.alabor.jdbccopier.database.meta;

import java.sql.Types;


/**
 * Describes a field of a {@link Table}.
 * 
 * @author Manuel Alabor
 * @see Types
 */
public class Field {

	private String name;
	private int type;
	
	/**
	 * Creates a new field with given name and type.
	 * 
	 * @param name
	 * @param type use the constants in {@link Types}
	 */
	public Field(String name, int type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Returns the SQL-type of this field.
	 * 
	 * @return
	 * @see Types
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the name of this field.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
}
