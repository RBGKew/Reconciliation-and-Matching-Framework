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
import static org.junit.Assert.fail;

import org.junit.Test;

public class ExactMatcherTest {

	@Test
	public void testNullMatches() {
		ExactMatcher matcher = new ExactMatcher();

		matcher.setNullToBlank(false);
		try {
			matcher.matches(null, null);
			fail("Expected NPE");
		}
		catch (NullPointerException e) {
		}
		catch (Exception e) {
			fail("Unexpected exception "+e);
		}

		matcher.setNullToBlank(true);
		try {
			assertTrue(matcher.matches(null, null));
			assertTrue(matcher.matches("", null));
			assertTrue(matcher.matches(null, ""));
		}
		catch (Exception e) {
			fail("Unexpected exception "+e);
		}
	}

	@Test
	public void testBlankMatches() {
		ExactMatcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("", ""));
		assertFalse(matcher.matches("", "x"));
		assertFalse(matcher.matches("x", ""));
	}

	@Test
	public void testStringMatches() {
		ExactMatcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("one", "one"));
	}

	@Test
	public void testStringCaseMatches() {
		ExactMatcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "One"));
	}

	@Test
	public void testStringTrimMatches() {
		ExactMatcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "one "));
	}

	@Test
	public void testStringWithWhitespaces () {
		ExactMatcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("hello kitty", "hello kitty"));
	}
}
