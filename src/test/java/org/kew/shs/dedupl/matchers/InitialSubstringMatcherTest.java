package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InitialSubstringMatcherTest {

	@Test
	public void testYearEqualsExactly () {
		Matcher yearMatcher = new InitialSubstringMatcher();
		assertEquals(true, yearMatcher.matches("2012", "2012"));
	}

	@Test
	public void testYearEqualsThreeDigits () {
		InitialSubstringMatcher yearMatcher = new InitialSubstringMatcher();
		// NOTE: if prefixSize is not set, it appears to return *always* true..
		yearMatcher.setPrefixSize(4);
		assertEquals(false, yearMatcher.matches("2012", "2013"));
		yearMatcher.setPrefixSize(3);
		assertEquals(true, yearMatcher.matches("2012", "2023"));
	}
}
