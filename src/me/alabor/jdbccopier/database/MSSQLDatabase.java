package me.alabor.jdbccopier.database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.database.meta.Field;
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
		return getTables(new ArrayList<String>(0), new ArrayList<String>(0));
	}
	
	@Override
	public List<Table> getTables(List<String> includes, List<String> excludes) {
		String filter = prepareIncludeAndExcludeClause(includes, excludes);
		List<Table> tables = new ArrayList<Table>();
		String query = "SELECT TABLE_CATALOG, TABLE_NAME, TABLE_SCHEMA FROM information_schema.tables WHERE TABLE_TYPE=?" + filter;
		String tableType = "BASE TABLE";
		
		try {
			PreparedStatement statement = getConnection().prepareStatement(query);
			statement.setString(1, tableType);
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				String catalog = result.getString("TABLE_CATALOG");
				String schema = result.getString("TABLE_SCHEMA");
				String name = result.getString("TABLE_NAME");
				
				Table table = new Table(catalog, schema, name);
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
	public boolean beforeTableCopy(Table table, Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) {
			result = setIdentityInsert(table, true);
		}
		
		return result; 
	}
	
	@Override
	public boolean afterTableCopy(Table table, Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) {
			result = setIdentityInsert(table, false);
		}
		
		return result;
	}
	
	@Override
	public String buildTableName(Table table) {
		return "["+table.getSchema()+"].["+table.getName()+"]";
	}
	
	@Override
	public boolean beforeCopy(Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) {
			result = enableConstraints(false);
		}
		
		return result;
	}
	
	@Override
	public boolean afterCopy(Mode mode) {
		boolean result = true;
		if(mode == Mode.Target) {
			result = enableConstraints(true);
		}
		
		return result;
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
	
	
	// Helpers -----------------------------------------------------------------
	public Connection getConnection() {
		return this.connection;
	}
	
	private void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Creates a {@link String} for table name filtering.
	 * 
	 * @param nameFilters
	 * @return
	 */
	private String prepareIncludeAndExcludeClause(List<String> includes, List<String> excludes) {
		String filter = "";
		
		filter = prepareFilterClause(" AND TABLE_NAME IN(", includes);
		filter = filter + prepareFilterClause(" AND TABLE_NAME NOT IN(", excludes);
		
		return filter;
	}

	/**
	 * Generates a filter clause for {@link #prepareIncludeAndExcludeClause(List, List)}
	 * 
	 * @param clause
	 * @param includes
	 * @return
	 */
	private String prepareFilterClause(String clause, List<String> includes) {
		String filter = "";
		
		if(includes.size() > 0) {
			StringBuffer buffer = new StringBuffer(clause);
			for(int i = 0, l = includes.size(); i < l; i++) {
				if(i > 0) buffer.append(",");
				buffer.append("'");
				buffer.append(includes.get(i));
				buffer.append("'");
			}
			buffer.append(")");
			filter = buffer.toString();
		}
		
		return filter;
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
			DatabaseMetaData meta = getConnection().getMetaData();
			ResultSet columns = meta.getColumns(null, null, table.getName(), null);
			
			while(columns.next()) {
				Field field = new Field(columns.getString("COLUMN_NAME"), columns.getInt("DATA_TYPE"));
				fields.add(field);				
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	/**
	 * Enables or disables constraints on all tables for this sql server.
	 * 
	 * @param enabled
	 * @return
	 */
	private boolean enableConstraints(boolean enabled) {
		boolean result = true;
		String query = "EXEC sp_msforeachtable \"ALTER TABLE ? NOCHECK CONSTRAINT all\"";
		if(enabled) query = "EXEC sp_msforeachtable \"ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all\"";
		
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
