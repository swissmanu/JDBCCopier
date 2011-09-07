package me.alabor.jdbccopier.copier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.copier.listener.CopierListener;
import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.FieldType;
import me.alabor.jdbccopier.database.meta.Table;

public abstract class AbstractCopier implements Copier {

	private List<CopierListener> listeners = new ArrayList<CopierListener>();

	public AbstractCopier() {
		super();
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
	
	protected void fireStartCopy(List<Table> tables) {
		for (CopierListener listener : listeners) {
			listener.startCopy(tables);
		}
	}	
	
	protected void fireEndCopy(int totalSuccess, int totalError) {
		for (CopierListener listener : listeners) {
			listener.endCopy(totalSuccess, totalError);
		}
	}

}