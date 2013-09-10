package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CommonTokensMatcherTest {

	@Test
	public void testMatchesExactly () throws Exception {
		Matcher matcher = new CommonTokensMatcher();
		assertTrue(matcher.matches("abc 123!", "abc 123!"));
	}

	@Test
	public void testCommonTokens () throws Exception {
		CommonTokensMatcher matcher = new CommonTokensMatcher();
		// default acceptance is a ratio of 0.5
		assertTrue(matcher.matches("first sec third", "first sec diff"));
		assertFalse(matcher.matches("first sec third", "first diff diff"));
		matcher.setMinRatio(0.2);
		assertTrue(matcher.matches("first sec third", "first diff diff"));
	}
	
	@Test
	public void testDelimiter() throws Exception {
		CommonTokensMatcher matcher = new CommonTokensMatcher();
		assertFalse(matcher.matches("USA", "U.S.A."));
		matcher.setDelimiter("");
		assertTrue(matcher.matches("USA", "U.S.A."));
		assertTrue(matcher.matches("abc", "abcd"));
	}
}
