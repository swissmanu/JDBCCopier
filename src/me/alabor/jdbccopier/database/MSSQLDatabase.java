package me.alabor.jdbccopier.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.FieldType;
import me.alabor.jdbccopier.database.meta.Table;


/**
 * A {@link Database} implementation for the Microsoft SQL Server 2005.
 * 
 * @author Manuel Alabor
 * @see Database
 */
public class MSSQLDatabase implements Database {

	private String connectionString;
	private Connection connection = null;
	
	public MSSQLDatabase(String connectionString) {
		this.connectionString = connectionString;
	}
	
	public void connect() throws Exception {
		if(getConnection() == null) {
			Connection connection = null;
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connection = DriverManager.getConnection(connectionString);
			connection.setAutoCommit(true);
			
			setConnection(connection);			
		}
	}
	
	@Override
	public List<Table> getTables() {
		List<Table> tables = new ArrayList<Table>();
		String query = "SELECT TABLE_NAME, TABLE_SCHEMA FROM information_schema.tables WHERE TABLE_TYPE=?";
		String tableType = "BASE TABLE";
		
		try {
			PreparedStatement statement = getConnection().prepareStatement(query);
			statement.setString(1, tableType);
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				String schema = result.getString("TABLE_SCHEMA");
				String name = result.getString("TABLE_NAME");
				
				Table table = new Table(schema, name);
				table.setFields(getFieldsForTable(table)); // important :)
				tables.add(table);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tables;
	}
	
	
	@Override
	public int countContentsForTable(Table table) {
		int count = 0;
		
		String query = "SELECT count(1) FROM " + buildTableName(table);
		
		try {
			Statement statement = getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			result.next();
			count = result.getInt(1);
		} catch(SQLException e) { }

		
		return count;
	}
	@Override
	public ResultSet getContentsForTable(Table table) throws SQLException {
		String query = "SELECT * FROM " + buildTableName(table);
		Statement statement = getConnection().createStatement();
		ResultSet result = statement.executeQuery(query);
		
		return result;
	}
	
	@Override
	public boolean beforeCopy(Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) result = enableConstraints(false);
		
		return result; 
	}
	
	@Override
	public boolean afterCopy(Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) result = enableConstraints(true);
		
		return result; 
	}
	
	@Override
	public boolean beforeTableCopy(Table table, Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) result = setIdentityInsert(table, true);
		
		return result; 
	}
	
	@Override
	public boolean afterTableCopy(Table table, Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) result = setIdentityInsert(table, false);
		
		return result;
	}
	
	@Override
	public String buildTableName(Table table) {
		return "["+table.getSchema()+"].["+table.getName()+"]";
	}
	
	@Override
	public PreparedStatement buildPreparedInsertStatement(Table table) throws Exception {
		List<Field> fields = table.getFields();
		StringBuffer query = new StringBuffer("INSERT INTO ");
		StringBuffer values = new StringBuffer("VALUES(");
		
		query.append(buildTableName(table));
		query.append(" (");
		
		for(int i = 0, l = fields.size(); i < l; i++) {
			Field field = fields.get(i);
			
			query.append("[");
			query.append(field.getName());
			query.append("]");
			
			values.append("?");
			
			if(i < l-1){
				query.append(",");
				values.append(",");
			}
		}
		
		query.append(") ");
		values.append(")");
		query.append(values);
		
		return createPreparedStatement(query.toString());
	}
	
	@Override
	public FieldType mapFieldType(String stringFieldType) {
		FieldType fieldType = null;
		
		if(stringFieldType.equals("bigint")) {
			fieldType = FieldType.BigInt;
		} else if(stringFieldType.equals("bit")) {
			fieldType = FieldType.Bit;
		} else if(stringFieldType.equals("char")) {
			fieldType = FieldType.Character;
		} else if(stringFieldType.equals("datetime")) {
			fieldType = FieldType.Date;
		} else if(stringFieldType.equals("image")) {
			fieldType = FieldType.BitVarying;  // ??
		} else if(stringFieldType.equals("int")) {
			fieldType = FieldType.Integer;
		} else if(stringFieldType.equals("numeric")) {
			fieldType = FieldType.Numeric;
		} else if(stringFieldType.equals("nvarchar")) {
			fieldType = FieldType.CharacterVarying;
		} else if(stringFieldType.equals("text")) {
			fieldType = FieldType.Character;
		} else if(stringFieldType.equals("uniqueidentifier")) {
			fieldType = FieldType.Character;
		} else if(stringFieldType.equals("varbinary")) {
			fieldType = FieldType.BitVarying;
		} else if(stringFieldType.equals("varchar")) {
			fieldType = FieldType.Character;
		}
		
		return fieldType;
	}
	
	
	// Helpers -----------------------------------------------------------------
	public Connection getConnection() {
		return this.connection;
	}
	
	private void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Delivers a list with {@link Field}'s for the given {@link Table}.
	 * 
	 * @param table
	 * @return
	 */
	private List<Field> getFieldsForTable(Table table) {
		List<Field> fields = new ArrayList<Field>();
		
		try {
			String query = "SELECT COLUMN_NAME, DATA_TYPE FROM information_schema.columns WHERE TABLE_SCHEMA=?, TABLE_NAME=?";
			PreparedStatement statement = getConnection().prepareStatement(query);
			statement.setString(1, table.getSchema());
			statement.setString(2, table.getName());
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				Field field = new Field(result.getString("COLUMN_NAME"), mapFieldType(result.getString("DATA_TYPE")));
				fields.add(field);
			}	
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	/**
	 * Enables or disables constraints on the sql server.
	 * 
	 * @param enabled
	 * @return
	 */
	private boolean enableConstraints(boolean enabled) {
		boolean result = true;
		String query = "EXEC sp_msforeachtable \"ALTER TABLE ? NOCHECK CONSTRAINT all\"";
		if(enabled) query = "EXEC sp_msforeachtable @command1=\"print '?'\", @command2=\"ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all\"";
		
		try {
			Statement statement = getConnection().createStatement();
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Makes it possible to overwrite columns with the <code>IDENTITY</code> flag.
	 * 
	 * @param table
	 * @param enabled
	 * @return
	 */
	private boolean setIdentityInsert(Table table, boolean enabled) {
		boolean result = true;
		String query = "SET IDENTITY_INSERT " + buildTableName(table);
		
		if(enabled) query += "ON";
		else query += "OFF";
		
		try {
			Statement statement = getConnection().createStatement();
			statement.execute(query);
		} catch (SQLException e) { }
		
		return result;
	}
	
	private PreparedStatement createPreparedStatement(String query) throws SQLException {
		PreparedStatement statement = getConnection().prepareStatement(query);
		return statement;
	}
}
