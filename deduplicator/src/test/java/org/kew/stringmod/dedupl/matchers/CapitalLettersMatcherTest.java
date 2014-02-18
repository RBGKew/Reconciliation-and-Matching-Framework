package org.kew.stringmod.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.kew.stringmod.dedupl.matchers.CapitalLettersMatcher;

public class CapitalLettersMatcherTest {

	@Test
	public void testMatchesExactly () throws Exception {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		matcher.setMinRatio(1);
		assertTrue(matcher.matches("Abraham Lincoln came to Ealing", "Abraham Lincoln came to Ealing"));
		// Also true would be:
		assertTrue(matcher.matches("Abraham Lincoln came to Ealing", "After Laughter comes the End"));
	}

	@Test
	public void testSameWordsDifferentCapitals () throws Exception {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertFalse(matcher.matches("My horse has MANY BIG teeth!", "My horse has many big teeth!"));
	}

	@Test
	public void testHm () throws Exception {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertTrue(matcher.matches("AbCdEEfG", "Ab Cd Eef G"));
		matcher.setMinRatio(1);
		assertFalse(matcher.matches("AbCdEEfG", "Ab Cd Eef G"));
	}
	
	@Test
	public void testAboutDots() throws Exception {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertFalse(matcher.matches("USA", "U.S.A"));
		matcher.setDelimiter("");
		assertTrue(matcher.matches("USA", "U.S.A"));
	}
}
