package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class CapitalLettersMatcherTest {

	@Test
	public void testMatchesExactly () {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		matcher.setMinRatio(1);
		assertTrue(matcher.matches("Abraham Lincoln came to Ealing", "Abraham Lincoln came to Ealing"));
		// Also true would be:
		assertTrue(matcher.matches("Abraham Lincoln came to Ealing", "After Laughter comes the End"));
	}

	@Test
	public void testSameWordsDifferentCapitals () {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertFalse(matcher.matches("My horse has MANY BIG teeth!", "My horse has many big teeth!"));
	}

	@Test
	public void testHm () {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertTrue(matcher.matches("AbCdEEfG", "Ab Cd Eef G"));
		matcher.setMinRatio(1);
		assertFalse(matcher.matches("AbCdEEfG", "Ab Cd Eef G"));
	}
}
