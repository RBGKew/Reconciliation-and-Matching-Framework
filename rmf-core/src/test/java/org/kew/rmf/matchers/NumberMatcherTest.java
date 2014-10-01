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

public class NumberMatcherTest {

	@Test
	public void testMatchesExactly () throws Exception {
		NumberMatcher matcher = new NumberMatcher();
		assertTrue(matcher.matches("Abraham Lincoln came 1834 to Ealing, 4 times in a row, riding 23 horses",
				"Abraham Lincoln came 1934 to Ealing, 4 times in a row, riding 23 horses"));
		// Also true would be:
		assertTrue(matcher.matches("Abraham Lincoln came 1834 to Ealing, 4 times in a row",
				"1834 was the year of the goose in chinese calendar. 4 gueese where beatified."));
	}

	@Test
	public void noNumbersButRestMatchesExactly () throws Exception {
		NumberMatcher matcher = new NumberMatcher();
		assertTrue(matcher.matches("No numbers at all here!", "No numbers at all here!"));
	}

	@Test
	public void noNumbersButRestMatchesNotExactly () throws Exception {
		NumberMatcher matcher = new NumberMatcher();
		assertFalse(matcher.matches("No numbers at all here!", "No numbers at all here neither!"));
	}

	@Test
	public void testMatchesWhitespaceTest () throws Exception {
		NumberMatcher matcher = new NumberMatcher();
		assertFalse(matcher.matches("123", "1 2 3"));
		assertTrue(matcher.matches("1a2b3", "1 2 3"));
	}

	@Test
	public void testChangeMinRatio () throws Exception {
		NumberMatcher matcher = new NumberMatcher();
		// minRatio expected to be (1)/1+(1+2) == 0.25, which is below the allowed limit (default==0.5)
		assertFalse(matcher.matches("1 23", "1 2 3"));
		matcher.setMinRatio(0.25);
		assertTrue(matcher.matches("1 23", "1 2 3"));
	}
}
