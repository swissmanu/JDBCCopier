package me.alabor.jdbccopier.database.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import me.alabor.jdbccopier.database.Database;

public class DatabaseFactory {

	/**
	 * Creates a {@link Database} instance for the given type using the provided
	 * connection string.
	 * 
	 * @param databaseType
	 * @param connectionString
	 * @see Database
	 * @return
	 */
	public Database createDatabase(String databaseType, String connectionString) {
		Database database = null;
		
		try {
			@SuppressWarnings("unchecked")
			Class<Database> databaseClass = (Class<Database>) Class.forName(databaseType);
			Constructor<?> ctor = databaseClass.getConstructor(String.class);
			database = (Database)ctor.newInstance(connectionString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return database;
	}
	
}
