package me.alabor.jdbccopier.copier;

import java.util.Queue;

import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.meta.Table;

public class PooledCopier extends AbstractCopier {

	private Queue<Table> tablePool;
	
	public PooledCopier(Database source, Database target, Queue<Table> tablePool) {
		super(source, target);
		this.tablePool = tablePool;
	}
	
	@Override
	public Table getNextTable() {
		Table table = null;
		synchronized (tablePool) {
			table = tablePool.poll();
		}
		
		return table;
	}

}
