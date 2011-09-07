package me.alabor.jdbccopier.copier.listener;

import me.alabor.jdbccopier.copier.TableListCopier;
import me.alabor.jdbccopier.database.meta.Table;

public interface CopierListener {

	/**
	 * Notifies that the {@link TableListCopier} begins now to copy the {@link Table} table
	 * 
	 * @param table
	 * @param totalRows
	 */
	public void startCopyTable(Table table, long totalRows);
	
	/**
	 * Provides information about the progress during copying the {@link Table}
	 * table.
	 * 
	 * @param table
	 * @param currentPos
	 * @param totalRows
	 */
	public void copyTableStatus(Table table, long currentPos, long totalRows);
	
	/**
	 * Error happend during copy the {@link Table} table.
	 * 
	 * @param table
	 * @param exception
	 */
	public void error(Table table, Exception exception);
	
	/**
	 * Notifies that the {@link TableListCopier} has finished the copy of {@link Table}
	 * table.
	 * 
	 * @param table
	 */
	public void endCopyTable(Table table);
	
	/**
	 * Indicates that the copy of tables will begin.
	 */
	public void startCopy();
	
	/**
	 * Notifies that the {@link TableListCopier} has completed its actions.
	 * 
	 * @param totalSuccess Statistics
	 * @param totalError Statistics
	 */
	public void endCopy(int totalSuccess, int totalError);
	
}
