package me.alabor.jdbccopier.database.meta;

public class Field {

	private String name;
	private FieldType type;
	
	public Field(String name, FieldType type) {
		this.name = name;
		this.type = type;
	}

	public FieldType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
}
