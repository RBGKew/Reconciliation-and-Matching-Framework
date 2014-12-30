/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		assertTrue(matcher.matches("My horse has MANY big teeth!", "My horse has many big teeth!"));
		matcher.setDelimiter("");
		assertFalse(matcher.matches("My horse has MANY big teeth!", "My horse has many big teeth!"));

		assertTrue(matcher.matches("A A A A B C", "A A A A D E"));
		matcher.setDeduplicateTokens(true);
		assertFalse(matcher.matches("A A A A B C", "A A A A D E"));
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
