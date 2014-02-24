package org.kew.stringmod.dedupl.matchers;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntegerMatcherTest {

	@Test
	public void test() {
		IntegerMatcher matcher = new IntegerMatcher();
		assertTrue(matcher.matches("10", "15"));
		assertFalse(matcher.matches("10", "16"));
		matcher.setMaxDiff(6);
		assertTrue(matcher.matches("10", "16"));
	}

}
