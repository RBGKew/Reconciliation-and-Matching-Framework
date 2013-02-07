package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ExactMatchingMatcherTest {

	@Test
	public void testNullMatches() {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches(null, null));
	}

	@Test
	public void testBlankMatches() {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("", ""));
	}

	@Test
	public void testNullBlankMatches() {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("", null));
		assertFalse(matcher.matches(null,""));
	}

	@Test
	public void testStringMatches() {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("one", "one"));
	}

	@Test
	public void testStringCaseMatches() {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "One"));
	}

	@Test
	public void testStringTrimMatches() {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "one "));
	}

}