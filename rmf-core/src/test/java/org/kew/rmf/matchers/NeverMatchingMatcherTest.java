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

import org.junit.Test;

public class NeverMatchingMatcherTest {

	@Test
	public void testNullMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches(null, null));
	}

	@Test
	public void testBlankMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", ""));
	}

	@Test
	public void testNullBlankMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", null));
	}

	@Test
	public void testStringMatches() throws Exception {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("one", "one"));
	}


}
