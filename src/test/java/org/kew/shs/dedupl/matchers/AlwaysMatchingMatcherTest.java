package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AlwaysMatchingMatcherTest {

	@Test
	public void testNullMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches(null, null));
	}

	public void testBlankMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("", ""));
	}

	public void testNullBlankMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("", null));
	}

	public void testStringMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("one", "two"));
	}

}