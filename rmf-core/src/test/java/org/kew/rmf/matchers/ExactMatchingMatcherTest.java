package org.kew.rmf.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExactMatchingMatcherTest {

	@Test
	public void testNullMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches(null, null));
	}

	@Test
	public void testBlankMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("", ""));
	}

	@Test
	public void testNullBlankMatches() throws Exception {
		ExactMatcher matcher = new ExactMatcher();

		assertTrue(matcher.matches("", null));
		assertTrue(matcher.matches(null,""));

		matcher.setBlanksMatch(true); // Default
		assertTrue(matcher.matches("", null));
		assertTrue(matcher.matches(null,""));

		matcher.setBlanksMatch(false);
		assertFalse(matcher.matches("", null));
		assertFalse(matcher.matches(null,""));
	}

	@Test
	public void testStringMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("one", "one"));
	}

	@Test
	public void testStringCaseMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "One"));
	}

	@Test
	public void testStringTrimMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "one "));
	}

	@Test
	public void testStringWithWhitespaces () throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("hello kitty", "hello kitty"));
	}
}
