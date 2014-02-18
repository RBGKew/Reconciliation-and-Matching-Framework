package org.kew.stringmod.dedupl.matchers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kew.stringmod.dedupl.matchers.AlwaysMatchingMatcher;
import org.kew.stringmod.dedupl.matchers.Matcher;

public class AlwaysMatchingMatcherTest {

	@Test
	public void testNullMatches() throws Exception {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches(null, null));
	}

	public void testBlankMatches() throws Exception {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("", ""));
	}

	public void testNullBlankMatches() throws Exception {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("", null));
	}

	public void testStringMatches() throws Exception {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("one", "two"));
	}

}