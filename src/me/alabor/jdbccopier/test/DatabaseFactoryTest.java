package me.alabor.jdbccopier.test;

import junit.framework.TestCase;
import me.alabor.jdbccopier.database.factory.DatabaseFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseFactoryTest extends TestCase {

	private DatabaseFactory factory;
	
	private final static String DATABASETYPE_MSSQL = "me.alabor.jdbccopier.database.MSSQLDatabase";
	
	@Before
	public void prepareFactory() {
		this.factory = new DatabaseFactory();
	}
	
	@Test
	public void testCreateMSSQLDatabase() {
		Assert.assertNotNull(getFactory().createDatabase(DATABASETYPE_MSSQL, ""));
	}
	
	private DatabaseFactory getFactory() {
		return factory;
	}

}
