package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class NumberMatcherTest {

	@Test
	public void testMatchesExactly () {
		NumberMatcher matcher = new NumberMatcher();
		assertTrue(matcher.matches("Abraham Lincoln came 1834 to Ealing, 4 times in a row",
				"Abraham Lincoln came 1934 to Ealing, 4 times in a row"));
		// Also true would be:
		assertTrue(matcher.matches("Abraham Lincoln came 1834 to Ealing, 4 times in a row",
				"1834 was the year of the goose in chinese calendar. 4 gueese where beatified."));
	}

	@Test
	public void testMatchesWhitespaceTest () {
		NumberMatcher matcher = new NumberMatcher();
		assertFalse(matcher.matches("123", "1 2 3"));
		assertTrue(matcher.matches("1a2b3", "1 2 3"));
	}

}
