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

public class InitialSubstringMatcherTest {

	@Test
	public void matchBlanks() {
		InitialSubstringMatcher matcher = new InitialSubstringMatcher();
		// prefixSize is null and hence 0..
		// in no case blanks would match here
		assertFalse(matcher.matches("", ""));
		assertFalse(matcher.matches("", "hello"));

	}
	@Test
	public void exactMatch () throws Exception {
		Matcher matcher = new InitialSubstringMatcher();
		assertTrue(matcher.matches("2012", "2012"));
	}

	@Test
	public void PrefixSize () {
		InitialSubstringMatcher matcher = new InitialSubstringMatcher();
		assertTrue(matcher.matches("2012", "201something else"));
		assertTrue(matcher.matches("2012", "2013"));
		matcher.setPrefixSize(4);
		assertFalse(matcher.matches("2012", "2013"));
	}
}
