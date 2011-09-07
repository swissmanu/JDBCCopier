package me.alabor.jdbccopier.copier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.copier.listener.CopierListener;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.Mode;
import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.FieldType;
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
	
	@Override
	public PreparedStatement setPreparedStatementParameters(PreparedStatement statement, Table table, ResultSet contents) throws Exception {
		List<Field> fields = table.getFields();
		
		for (int i = 0, l = fields.size(); i < l; i++) {
			Field field = fields.get(i);
			FieldType type = field.getType();
			int parameterIndex = i+1;
			
			
			if(type == FieldType.BigInt) {
				statement.setLong(parameterIndex, contents.getLong(field.getName()));
			} else if(type == FieldType.Bit) {
				statement.setBoolean(parameterIndex, contents.getBoolean(field.getName()));
			} else if(type == FieldType.Character) {
				statement.setString(parameterIndex, contents.getString(field.getName()));
			} else if(field.getType() == FieldType.Date
					|| type == FieldType.Time
					|| type == FieldType.Timestamp) {
				statement.setDate(parameterIndex, contents.getDate(field.getName()));
			} else if(type == FieldType.BitVarying) {
				statement.setBytes(parameterIndex, contents.getBytes(field.getName()));
			} else if(type == FieldType.Integer) {
				statement.setInt(parameterIndex, contents.getInt(field.getName()));
			} else if(type == FieldType.Numeric) {
				statement.setBigDecimal(parameterIndex, contents.getBigDecimal(field.getName()));
			} else if(type == FieldType.CharacterVarying) {
				statement.setNString(parameterIndex, contents.getNString(field.getName()));
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
	public void addCopierListener(CopierListener copierListener) {
		if(!listeners.contains(copierListener)) {
			listeners.add(copierListener);
		}
	}

	public void removeCopierListener(CopierListener copierListener) {
		listeners.remove(copierListener);
	}
	
	protected void fireStartCopyTable(Table table, long totalRows) {
		for (CopierListener listener : listeners) {
			listener.startCopyTable(table, totalRows);
		}
	}

	protected void fireCopyTableStatus(Table table, long currentPos, long totalRows) {
		for (CopierListener listener : listeners) {
			listener.copyTableStatus(table, currentPos, totalRows);
		}
	}

	protected void fireError(Table table, Exception error) {
		for (CopierListener listener : listeners) {
			listener.error(table, error);
		}
	}
	
	protected void fireEndCopyTable(Table table) {
		for (CopierListener listener : listeners) {
			listener.endCopyTable(table);
		}
	}
	
	protected void fireStartCopy() {
		for (CopierListener listener : listeners) {
			listener.startCopy();
		}
	}	
	
	protected void fireEndCopy(int totalSuccess, int totalError) {
		for (CopierListener listener : listeners) {
			listener.endCopy(totalSuccess, totalError);
		}
	}

}