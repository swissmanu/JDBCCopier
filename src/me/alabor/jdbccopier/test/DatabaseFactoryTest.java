package me.alabor.jdbccopier.test;

import me.alabor.jdbccopier.database.factory.DatabaseFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseFactoryTest {

	private DatabaseFactory factory;
	
	private final static String DATABASETYPE_MSSQL = "me.alabor.jdbccopier.database.MSSQLDatabase";
	
	@Before
	public void prepareFactory() {
		this.factory = new DatabaseFactory();
	}
	
	@Test
	public void testCreateMSSQLDatabase() {
		Assert.assertNotNull(this.factory.createDatabase(DATABASETYPE_MSSQL, ""));
	}

}
