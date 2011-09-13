package me.alabor.jdbccopier.copier.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.copier.PooledCopier;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.factory.DatabaseFactory;
import me.alabor.jdbccopier.database.meta.Table;

public class CopierFactory {
	
	private DatabaseFactory databaseFactory;
	
	/**
	 * Creates a new {@link CopierFactory} using the given {@link DatabaseFactory}.
	 * 
	 * @param databaseFactory
	 */
	public CopierFactory(DatabaseFactory databaseFactory) {
		this.databaseFactory = databaseFactory;
	}

	/**
	 * Creates a {@link List} with {@link PooledCopier}'s for the given source
	 * and target database.
	 * 
	 * @param sourceDatabaseType
	 * @param sourceConnectionString
	 * @param targetDatabaseType
	 * @param targetConnectionString
	 * @param workerCount
	 * @param tablePool
	 * @return
	 */
	public List<Copier> createPooledCopiers(String sourceDatabaseType, String sourceConnectionString, String targetDatabaseType, String targetConnectionString, int workerCount, Queue<Table> tablePool) {
		List<Copier> copiers = new ArrayList<Copier>(workerCount);
		
		for (int i = 0; i < workerCount; i++) {
			Database sourceDatabase = getDatabaseFactory().createDatabase(sourceDatabaseType, sourceConnectionString);
			Database targetDatabase = getDatabaseFactory().createDatabase(targetDatabaseType, targetConnectionString);
			copiers.add(new PooledCopier(sourceDatabase, targetDatabase, tablePool));
		}
		
		return copiers;
	}
	
	private DatabaseFactory getDatabaseFactory() {
		return this.databaseFactory;
	}
	
}
