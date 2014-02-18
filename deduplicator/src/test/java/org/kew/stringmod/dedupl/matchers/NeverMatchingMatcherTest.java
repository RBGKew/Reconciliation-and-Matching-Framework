package org.kew.stringmod.dedupl.matchers;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.kew.stringmod.dedupl.matchers.Matcher;
import org.kew.stringmod.dedupl.matchers.NeverMatchingMatcher;

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
