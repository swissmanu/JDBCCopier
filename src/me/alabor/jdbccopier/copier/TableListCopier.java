package me.alabor.jdbccopier.copier;

import java.util.Iterator;
import java.util.List;

import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.meta.Table;

/**
 * Copies the content of a list of {@link Table}'s from one database to another
 * one.
 * 
 * @author Manuel Alabor
 */
public class TableListCopier extends AbstractCopier implements Copier {
	
	private Iterator<Table> tableIterator;
	
	public TableListCopier(Database source, Database target, List<Table> tablesToCopy) {
		super(source, target);
		this.tableIterator = tablesToCopy.iterator();
	}
	
	@Override
	public Table getNextTable() {
		Table table = null;
		if(tableIterator.hasNext()) table = tableIterator.next();
		
		return table;
	}
	
}