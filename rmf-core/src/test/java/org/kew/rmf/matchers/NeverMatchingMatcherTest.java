package org.kew.rmf.matchers;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class NeverMatchingMatcherTest {

	@Test
	public void testNullMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches(null, null));
	}

	@Test
	public void testBlankMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", ""));
	}

	@Test
	public void testNullBlankMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", null));
	}

	@Test
	public void testStringMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("one", "one"));
	}


}
