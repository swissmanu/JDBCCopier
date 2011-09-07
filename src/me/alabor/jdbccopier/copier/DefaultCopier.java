package me.alabor.jdbccopier.copier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.Mode;
import me.alabor.jdbccopier.database.meta.Table;

/**
 * Copies the content of a list of {@link Table}'s from one database to another
 * one.
 * 
 * @author Manuel Alabor
 */
public class DefaultCopier extends AbstractCopier implements Copier {
	
	private Database source = null;
	private Database target = null;
	private Iterator<Table> tableIterator;
	
	public DefaultCopier(Database source, Database target, List<Table> tablesToCopy) {
		super();
		this.source = source;
		this.target = target;
		this.tableIterator = tablesToCopy.iterator();
	}
	
	@Override
	public Table getNextTable() {
		Table table = null;
		if(tableIterator.hasNext()) table = tableIterator.next();
		
		return table;
	}
	
	@Override
	public void copy() {
		if(checkConnections()) {
			fireStartCopy();
			
			source.beforeCopy(Mode.Source);
			target.beforeCopy(Mode.Target);
			Table table = null;
			
			while((table = getNextTable()) != null) {
				long totalRows = source.countContentsForTable(table);
				
				fireStartCopyTable(table, totalRows);
				
				target.beforeTableCopy(table, Mode.Target);
				copyTableContents(table);
				target.afterTableCopy(table, Mode.Target);
				
				fireEndCopyTable(table);				
			}
			
			source.afterCopy(Mode.Source);
			target.afterCopy(Mode.Target);
		} else {
			fireError(null, new Exception("Connection problems"));
		}
		
		fireEndCopy(0,0);
	}
	
	/**
	 * Checks the database connection in source and target.
	 * 
	 * @return true/false regarding the connection states
	 */
	private boolean checkConnections() {
		boolean result = true;
		try {
			source.connect();
			target.connect();
		} catch(Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	private void copyTableContents(Table table) {
		int totalRowsOnSource = source.countContentsForTable(table);
		int totalProcessed = 0;
		
		try {
			PreparedStatement targetStatement = target.buildPreparedInsertStatement(table);
			ResultSet sourceContents = source.getContentsForTable(table);
			
			while(sourceContents.next()) {
				totalProcessed++;
				fireCopyTableStatus(table, totalProcessed, totalRowsOnSource);
				
				targetStatement = setPreparedStatementParameters(targetStatement, table, sourceContents);
				targetStatement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
