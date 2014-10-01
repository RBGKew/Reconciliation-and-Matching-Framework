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

public class NumberInRangeMatcherTest {

	@Test
	public void testMatches() throws Exception {
		NumberInRangeMatcher matcher = new NumberInRangeMatcher();

		assertTrue(matcher.matches("32", "32"));
		assertTrue(matcher.matches("32", "12 32"));
		assertTrue(matcher.matches("32", "32 9"));
		assertTrue(matcher.matches("32", "12 32-33 89"));
		assertTrue(matcher.matches("32", "12 31-32 89"));
		assertTrue(matcher.matches("32", "12 31-33 89"));
		assertTrue(matcher.matches("32", "12-18 30-35 45-90"));
		assertTrue(matcher.matches("1854", "Vols. 1-7, 1848-1868 etc."));

		assertFalse(matcher.matches("29", "32"));
		assertFalse(matcher.matches("29", "12 32"));
		assertFalse(matcher.matches("29", "32 9"));
		assertFalse(matcher.matches("29", "12 32-33 89"));
		assertFalse(matcher.matches("29", "12 31-32 89"));
		assertFalse(matcher.matches("29", "12 31-33 89"));
		assertFalse(matcher.matches("29", "12-18 30-35 45-90"));
		assertFalse(matcher.matches("1829", "Vols. 1-7, 1848-1868 etc."));

		assertTrue(matcher.matches("32", "12 32/33 89"));
		assertTrue(matcher.matches("33", "12 32/33 89"));
		assertTrue(matcher.matches("32", "12 31-3 89"));
		assertTrue(matcher.matches("1332", "12 1331-3 89"));
		assertTrue(matcher.matches("1332", "12 1331-33 89"));
		assertTrue(matcher.matches("1332", "12 1331-333 89"));

		assertFalse(matcher.matches("33", "12 32/34 89"));

		assertTrue(matcher.matches("31", "88 12+ 32/33 89"));
		assertTrue(matcher.matches("1888", "88 12+ 1832+ 89"));
	}
}
