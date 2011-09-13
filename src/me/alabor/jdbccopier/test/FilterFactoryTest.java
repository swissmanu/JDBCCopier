package me.alabor.jdbccopier.test;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import me.alabor.jdbccopier.copier.factory.FilterFactory;

import org.junit.Test;

public class FilterFactoryTest extends TestCase {

	@Test
	public void testCreateFilterList() {
		FilterFactory factory = new FilterFactory();
		String input = "this,is,a, test";
		List<String> list = factory.createFilterList(input);
		
		Assert.assertEquals(4, list.size());
		Assert.assertEquals("test", list.get(3));
	}

}
