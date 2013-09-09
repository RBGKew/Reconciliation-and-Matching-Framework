package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class NeverMatchingMatcherTest {

	@Test
	public void testNullMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches(null, null));
	}

	@Test
	public void testBlankMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", ""));
	}

	@Test
	public void testNullBlankMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", null));
	}

	@Test
	public void testStringMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("one", "one"));
	}


}
