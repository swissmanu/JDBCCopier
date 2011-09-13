package me.alabor.jdbccopier.copier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.copier.listener.CopierListener;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.Mode;
import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.Table;

public abstract class AbstractCopier implements Copier {

	private List<CopierListener> listeners = new ArrayList<CopierListener>();
	private Database source = null;
	private Database target = null;

	public AbstractCopier(Database source, Database target) {
		super();
		this.source = source;
		this.target = target;
	}
	
	@Override
	/**
	 * Copies the contents of the tables in <code>source</code> into the matching
	 * tables of <code>target</code>.
	 */
	public void copy() {
		if(checkConnections()) {
			fireStartCopy();
			
			source.beforeCopy(Mode.Source);
			target.beforeCopy(Mode.Target);
			Table table = null;
			
			while((table = getNextTable()) != null) {
				long totalRows = source.countContentsForTable(table);
				
				fireStartCopyTable(table, totalRows);
				
				source.beforeTableCopy(table, Mode.Source);
				target.beforeTableCopy(table, Mode.Target);
				copyTableContents(table);
				target.afterTableCopy(table, Mode.Target);
				source.afterTableCopy(table, Mode.Source);
				
				fireEndCopyTable(table);
			}
			
			target.afterCopy(Mode.Target);
			source.afterCopy(Mode.Source);
		} else {
			fireError(null, new Exception("Connection problems"));
		}
		
		fireEndCopy(0,0);
	}
	
	@Override
	public PreparedStatement setPreparedStatementParameters(PreparedStatement statement, Table table, ResultSet contents) throws Exception {
		List<Field> fields = table.getFields();
		
		for (int i = 0, l = fields.size(); i < l; i++) {
			Field field = fields.get(i);
			int parameterIndex = i+1;
			
			Object value = contents.getObject(field.getName());
			if(value != null) {
				statement.setObject(parameterIndex, value, field.getType());
			} else {
				statement.setNull(parameterIndex, field.getType());
			}
		}
		
		return statement;
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

	/**
	 * Copies the contents of a table from source to target {@link Database}.
	 * 
	 * @param table
	 */
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
	
	
	// Event-Handling ----------------------------------------------------------
	@Override
	public void addCopierListener(CopierListener copierListener) {
		if(!listeners.contains(copierListener)) {
			listeners.add(copierListener);
		}
	}


	@Override
	public void removeCopierListener(CopierListener copierListener) {
		listeners.remove(copierListener);
	}
	
	/**
	 * Fires a StartCopyTable event to all attached {@link CopierListener}'s.
	 * 
	 * @param table
	 * @param totalRows
	 */
	protected void fireStartCopyTable(Table table, long totalRows) {
		for (CopierListener listener : listeners) {
			listener.startCopyTable(table, totalRows);
		}
	}

	/**
	 * Fires a CopyTableStatus event to all attached {@link CopierListener}'s.
	 * 
	 * @param table
	 * @param currentPos
	 * @param totalRows
	 */
	protected void fireCopyTableStatus(Table table, long currentPos, long totalRows) {
		for (CopierListener listener : listeners) {
			listener.copyTableStatus(table, currentPos, totalRows);
		}
	}

	/**
	 * Fires an Error event to all attached {@link CopierListener}'s.
	 * 
	 * @param table
	 * @param error
	 */
	protected void fireError(Table table, Exception error) {
		for (CopierListener listener : listeners) {
			listener.error(table, error);
		}
	}
	
	/**
	 * Fires an EndCopyTable event to all attached {@link CopierListener}'s.
	 * 
	 * @param table
	 */
	protected void fireEndCopyTable(Table table) {
		for (CopierListener listener : listeners) {
			listener.endCopyTable(table);
		}
	}
	
	/**
	 * Fires a StartCopy event to all attached {@link CopierListener}'s.
	 */
	protected void fireStartCopy() {
		for (CopierListener listener : listeners) {
			listener.startCopy();
		}
	}
	
	/**
	 * Fires an EndCopy event to all attached {@link CopierListener}'s.
	 * 
	 * @param totalSuccess
	 * @param totalError
	 */
	protected void fireEndCopy(int totalSuccess, int totalError) {
		for (CopierListener listener : listeners) {
			listener.endCopy(totalSuccess, totalError);
		}
	}

}