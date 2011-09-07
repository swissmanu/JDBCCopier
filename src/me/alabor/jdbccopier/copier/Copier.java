package me.alabor.jdbccopier.copier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.Mode;
import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.FieldType;
import me.alabor.jdbccopier.database.meta.Table;

public class Copier extends AbstractCopier {

	private Database source = null;
	private Database target = null;
	private List<Table> tablesToCopy = new ArrayList<Table>();
	
	public Copier(Database source, Database target) {
		super();
		this.source = source;
		this.target = target;
	}
	
	public void setTablesToCopy(List<Table> tablesToCopy) {
		this.tablesToCopy = tablesToCopy;
	}
	
	public List<Table> getTablesToCopy() {
		return this.tablesToCopy;
	}
	
	public void copy() {
		if(checkConnections()) {
			List<Table> tables = getTablesToCopy();
			fireStartCopy(tables);
			
			source.beforeCopy(Mode.Source);
			target.beforeCopy(Mode.Target);
			for (int i = 0, l = tables.size(); i < l; i++) {
				Table table = tables.get(i);
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
	
	/**
	 * Sets the parameters of a {@link PreparedStatement} regarding the
	 * specific {@link FieldType}'s and returns the statement afterwards.
	 * 
	 * @param statement to prepare
	 * @param table
	 * @param contents to copy
	 * @return
	 * @throws Exception
	 */
	private PreparedStatement setPreparedStatementParameters(PreparedStatement statement, Table table, ResultSet contents) throws Exception {
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
			
			/*
			if(field.getType().equals("bigint")) {
				statement.setLong(parameterIndex, contents.getLong(field.getName()));
			} else if(field.getType().equals("bit")) {
				statement.setBoolean(parameterIndex, contents.getBoolean(field.getName()));
			} else if(field.getType().equals("char")) {
				statement.setString(parameterIndex, contents.getString(field.getName()));
			} else if(field.getType().equals("datetime")) {
				statement.setDate(parameterIndex, contents.getDate(field.getName()));
			} else if(field.getType().equals("image")) {
				statement.setBinaryStream(parameterIndex, contents.getBinaryStream(field.getName()));
			} else if(field.getType().equals("int")) {
				statement.setInt(parameterIndex, contents.getInt(field.getName()));
			} else if(field.getType().equals("numeric")) {
				statement.setBigDecimal(parameterIndex, contents.getBigDecimal(field.getName()));
			} else if(field.getType().equals("nvarchar")) {
				statement.setNString(parameterIndex, contents.getNString(field.getName()));
			} else if(field.getType().equals("text")) {
				statement.setString(parameterIndex, contents.getString(field.getName()));
			} else if(field.getType().equals("uniqueidentifier")) {
				statement.setString(parameterIndex, contents.getString(field.getName()));
			} else if(field.getType().equals("varbinary")) {
				statement.setBinaryStream(parameterIndex, contents.getBinaryStream(field.getName()));
			} else if(field.getType().equals("varchar")) {
				statement.setString(parameterIndex, contents.getString(field.getName()));
			}
			*/
		}
		
		return statement;
	}
	
}
