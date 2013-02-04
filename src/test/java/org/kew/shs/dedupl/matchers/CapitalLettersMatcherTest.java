package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CapitalLettersMatcherTest {

	@Test
	public void testMatchesExactly () {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		matcher.setMinRatio(1);
		assertEquals(true, matcher.matches("Abraham Lincoln came to Ealing", "Abraham Lincoln came to Ealing"));
		// Also true would be:
		assertEquals(true, matcher.matches("Abraham Lincoln came to Ealing", "After Laughter comes the End"));
	}

	@Test
	public void testSameWordsDifferentCapitals () {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertEquals(false, matcher.matches("My horse has MANY BIG teeth!", "My horse has many big teeth!"));
	}

	@Test
	public void testHm () {
		CapitalLettersMatcher matcher = new CapitalLettersMatcher();
		assertEquals(true, matcher.matches("AbCdEEfG", "Ab Cd Eef G"));
		matcher.setMinRatio(1);
		assertEquals(false, matcher.matches("AbCdEEfG", "Ab Cd Eef G"));
	}
}
