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

public class ExactMatchingMatcherTest {

	@Test
	public void testNullMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches(null, null));
	}

	@Test
	public void testBlankMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("", ""));
	}

	@Test
	public void testNullBlankMatches() throws Exception {
		ExactMatcher matcher = new ExactMatcher();

		assertTrue(matcher.matches("", null));
		assertTrue(matcher.matches(null,""));

		matcher.setBlanksMatch(true); // Default
		assertTrue(matcher.matches("", null));
		assertTrue(matcher.matches(null,""));

		matcher.setBlanksMatch(false);
		assertFalse(matcher.matches("", null));
		assertFalse(matcher.matches(null,""));
	}

	@Test
	public void testStringMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("one", "one"));
	}

	@Test
	public void testStringCaseMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "One"));
	}

	@Test
	public void testStringTrimMatches() throws Exception {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "one "));
	}

	@Test
	public void testStringWithWhitespaces () throws Exception {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("hello kitty", "hello kitty"));
	}
}
