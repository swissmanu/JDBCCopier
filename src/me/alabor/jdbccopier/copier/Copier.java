package me.alabor.jdbccopier.copier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.alabor.jdbccopier.copier.listener.CopierListener;
import me.alabor.jdbccopier.database.meta.Table;

public interface Copier {

	/**
	 * Starts the copy process. 
	 */
	public void copy();

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
	public PreparedStatement setPreparedStatementParameters(PreparedStatement statement, Table table, ResultSet contents) throws Exception;

	/**
	 * Provides the next {@link Table} which sould be copied.
	 * 
	 * @return Table
	 */
	public Table getNextTable();

	/**
	 * Removes a {@link CopierListener} from this {@link Copier}.
	 * 
	 * @param copierListener
	 */
	public abstract void removeCopierListener(CopierListener copierListener);

	/**
	 * Adds, if not already added, a new {@link CopierListener} to this
	 * {@link Copier}.
	 * 
	 * @param copierListener
	 */
	public abstract void addCopierListener(CopierListener copierListener);
	
}