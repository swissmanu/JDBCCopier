package me.alabor.jdbccopier.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import me.alabor.jdbccopier.database.meta.Table;

public interface Database {
	
	/**
	 * Connects to the database with the given connectionString.
	 * 
	 * @param connectionString
	 * @throws Exception
	 */
	public void connect() throws Exception;
	
	/**
	 * Delivers a List<Table> with the information of all tables of a database
	 * server.
	 * 
	 * @return
	 */
	public List<Table> getTables();
	
	/**
	 * Same as {@link #getTables()}, but with the possibility to pass {@link List}'s
	 * with {@link String}'s which describe what tables to include/exclude from
	 * the copy-process.
	 * 
	 * @param includes
	 * @param excludes
	 * @return
	 */
	public List<Table> getTables(List<String> includes, List<String> excludes);
	
	/**
	 * Counts the total rows on the {@link Table} table.
	 * 
	 * @param table
	 * @return
	 */
	public int countContentsForTable(Table table);
	
	/**
	 * Returns a {@link ResultSet} to the contents of a specific table.
	 * 
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getContentsForTable(Table table) throws Exception;
	
	
	/**
	 * Actions to run before the copy process is initiated.
	 * 
	 * @return
	 */
	public boolean beforeCopy(Mode mode);
	
	/**
	 * Actions to run after the copy process has ended.
	 * 
	 * @return
	 */
	public boolean afterCopy(Mode mode);
	
	
	/**
	 * Actions to run before the {@link Table} table gets copied.
	 * 
	 * @param table
	 * @param mode
	 * @return
	 */
	public boolean beforeTableCopy(Table table, Mode mode);
	
	/**
	 * Actions to run before the {@link Table} gets copied.
	 * 
	 * @param table
	 * @param mode
	 * @return
	 */
	public boolean afterTableCopy(Table table, Mode mode);
	
	
	/**
	 * Builds a technology specific table name from a {@link Table}.
	 * 
	 * @param table
	 * @return
	 */
	public String buildTableName(Table table);
	
	/**
	 * Create a {@link PreparedStatement} with an INSERT INTO query for the
	 * {@link Table} table.
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement buildPreparedInsertStatement(Table table) throws Exception;
	
}
