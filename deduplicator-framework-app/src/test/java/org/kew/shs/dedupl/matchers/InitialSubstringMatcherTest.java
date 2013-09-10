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
		assertTrue(matcher.matches("2012", "201something else"));
		assertTrue(matcher.matches("2012", "2013"));
		matcher.setPrefixSize(4);
		assertFalse(matcher.matches("2012", "2013"));
	}
}
