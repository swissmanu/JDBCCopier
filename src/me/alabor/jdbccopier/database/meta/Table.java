package me.alabor.jdbccopier.database.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {

	private String calaog;
	private String schema;
	private String name;
	private List<Field> fields = new ArrayList<Field>();
	
	public Table(String catalog, String schema, String table) {
		super();
		this.calaog = catalog;
		this.schema = schema;
		this.name = table;
	}
	
	public String getCalaog() {
		return calaog;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public String getName() {
		return name;
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public List<Field> getFields() {
		return Collections.unmodifiableList(fields);
	}
	
	@Override
	public String toString() {
		return getCalaog() + "." + getSchema() + "." + getName();
	}
	
}
