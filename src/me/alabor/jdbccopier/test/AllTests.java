package me.alabor.jdbccopier.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(FilterFactoryTest.class);
		suite.addTestSuite(DatabaseFactoryTest.class);
		//$JUnit-END$
		return suite;
	}

}
