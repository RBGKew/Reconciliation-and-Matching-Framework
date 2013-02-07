package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;


public class CommonTokensMatcherTest {

	@Test
	public void testMatchesExactly () {
		Matcher matcher = new CommonTokensMatcher();
		assertTrue(matcher.matches("abc 123!", "abc 123!"));
	}

	@Test
	public void testMatchesWhatAboutWhitespaceAndPunctuation () {
		Matcher matcher = new CommonTokensMatcher();
		// translates non-alphanumerics into whitespaces..
		assertTrue(matcher.matches("abc 123!", "abc?123!"));
		// ..which doesn't mean it ignores whitespaces at all!
		assertFalse(matcher.matches("abc123!", "abc?123!"));
		// double whitespaces are replace with one whitespace
		assertTrue(matcher.matches("abc 123!", "abc  123!"));
	}

	@Test
	public void testCommonTokens () {
		CommonTokensMatcher matcher = new CommonTokensMatcher();
		// default acceptance is a ratio of 0.5
		assertTrue(matcher.matches("first sec third", "first sec diff"));
		assertFalse(matcher.matches("first sec third", "first diff diff"));
		matcher.setMinRatio(0.2);
		assertTrue(matcher.matches("first sec third", "first diff diff"));
	}
}
