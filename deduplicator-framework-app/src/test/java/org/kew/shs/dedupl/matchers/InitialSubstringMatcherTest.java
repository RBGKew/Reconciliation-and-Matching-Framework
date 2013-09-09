package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class InitialSubstringMatcherTest {

	@Test
	public void matchBlanks() {
		InitialSubstringMatcher matcher = new InitialSubstringMatcher();
		// prefixSize is null and hence 0..
		// in no case blanks would match here
		assertFalse(matcher.matches("", ""));
		assertFalse(matcher.matches("", "hello"));

	}
	@Test
	public void exactMatch () throws Exception {
		Matcher matcher = new InitialSubstringMatcher();
		assertTrue(matcher.matches("2012", "2012"));
	}

	@Test
	public void PrefixSize () {
		InitialSubstringMatcher matcher = new InitialSubstringMatcher();
		// TODO: if prefixSize is not set, it appears be set to 0 and to return *always* true..
		assertTrue(matcher.matches("2012", "something else"));
		// if prefixSize => the length of at least one of the strings, it doesn't match
		// TODO: why equals?
		matcher.setPrefixSize(4);
		assertFalse(matcher.matches("2012", "2013"));
		// these two should match on the first three digits
		matcher.setPrefixSize(3);
		assertTrue(matcher.matches("2012", "2013"));
		// these should not match then
		assertFalse(matcher.matches("2012", "2022"));
	}
}
