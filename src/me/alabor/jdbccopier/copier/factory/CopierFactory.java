package me.alabor.jdbccopier.copier.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.copier.PooledCopier;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.meta.Table;

public class CopierFactory {

	/**
	 * @param source database
	 * @param target database
	 * @param workerCount How many copiers should be created?
	 * @param tablePool {@link Queue} with tables to copy.
	 * @return
	 */
	public List<Copier> createPooledCopiers(Database source, Database target, int workerCount, Queue<Table> tablePool) {
		List<Copier> copiers = new ArrayList<Copier>(workerCount);
		
		for (int i = 0; i < workerCount; i++) {
			copiers.add(new PooledCopier(source, target, tablePool));
		}
		
		return copiers;
	}
	
}
