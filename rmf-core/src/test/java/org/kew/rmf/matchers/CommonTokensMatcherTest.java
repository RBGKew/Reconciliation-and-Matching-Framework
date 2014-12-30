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

public class CommonTokensMatcherTest {

	@Test
	public void testMatchesExactly() {
		CommonTokensMatcher matcher = new CommonTokensMatcher();
		assertTrue("Exact match", matcher.matches("abc 123!", "abc 123!"));
	}

	@Test
	public void testCommonTokens() {
		CommonTokensMatcher matcher = new CommonTokensMatcher();
		// default acceptance is a ratio of 0.5
		assertTrue("Common tokens", matcher.matches("first sec third", "first sec diff"));
		assertTrue("Common tokens", matcher.matches("first sec third", "diff first sec"));
		assertFalse("Common tokens", matcher.matches("first sec third", "first diff diff"));
		matcher.setMinRatio(0.2);
		assertTrue("Common tokens", matcher.matches("first sec third", "first diff diff"));
	}

	@Test
	public void testDelimiter() {
		CommonTokensMatcher matcher = new CommonTokensMatcher();
		assertFalse("Delimiter", matcher.matches("USA", "U.S.A."));
		matcher.setDelimiter("");
		assertTrue("Delimiter", matcher.matches("USA", "U.S.A."));
		assertTrue("Delimiter", matcher.matches("abc", "abcd"));
	}

	@Test
	public void testVaryingTokenNumbers() {
		CommonTokensMatcher matcher = new CommonTokensMatcher();

		matcher.setMinRatio(1);
		assertTrue("Varying token numbers 1", matcher.matches("a b c", "a b c"));

		matcher.setMinRatio(0.5);
		// ratio == 1 still matches
		assertTrue("Varying token numbers 2", matcher.matches("a b c", "a b c"));

		assertTrue("Varying token numbers 3", matcher.matches("a b", "a b c")); // 5 tokens, 4 common, ratio = ⅘
		assertTrue("Varying token numbers 4", matcher.matches("a", "a b c")); // 4 tokens, 2 common, ratio = ½
		assertTrue("Varying token numbers 5", matcher.matches("a b c", "a b")); // 5 tokens, 4 common, ratio = ⅘

		assertTrue("Varying token numbers 6", matcher.matches("a b d", "a b c")); // 6 tokens, 4 common, ratio = ⅔
		assertTrue("Varying token numbers 7", matcher.matches("a b c", "a b d")); // 6 tokens, 4 common, ratio = ⅔

		assertTrue("Varying token numbers 8", matcher.matches("a b d e", "a b c")); // 7 tokens, 4 common, ratio = ⁴⁄₇
		assertTrue("Varying token numbers 9", matcher.matches("a b c", "a b d e")); // 7 tokens, 4 common, ratio = ⁴⁄₇

		// The total number of tokens (for the denominator of the ratio) is the sum of the number of tokens on each side.
		matcher.setMinRatio(0.2500000001);
		assertFalse("Varying token numbers 10", matcher.matches("a b", "a b c d e f g h i j k l m n")); // 16 tokens, 4 common, ratio = ¼
		assertFalse("Varying token numbers 11", matcher.matches("a b c d e f g h i j k l m n", "a b")); // 16 tokens, 4 common, ratio = ¼

		matcher.setMinRatio(0.25);
		assertTrue("Varying token numbers 12", matcher.matches("a b", "a b c d e f g h i j k l m n")); // 16 tokens, 4 common, ratio = ¼
		assertTrue("Varying token numbers 13", matcher.matches("a b c d e f g h i j k l m n", "a b")); // 16 tokens, 4 common, ratio = ¼
	}

	@Test
	public void testDuplicateTokens() {
		CommonTokensMatcher matcher = new CommonTokensMatcher();

		// Default is that duplicate tokens must each be matched
		matcher.setMinRatio(0.4);
		assertTrue("Duplicate tokens 1", matcher.matches("a a", "a b c")); // 5 tokens, 2 are common, ratio = ⅖
		assertFalse("Duplicate tokens 2", matcher.matches("a a", "a b c d")); // 6 tokens, 2 are common, ratio = ⅓
		assertTrue("Duplicate tokens 3", matcher.matches("a a", "a a c d")); // 6 tokens, 4 are common, ratio = ⅔

		// Tokens can be deduplicated within each side first
		matcher.setDeduplicateTokens(true);
		assertTrue("Deduplicate tokens 1", matcher.matches("a a", "a b c")); // 1+3 unique tokens, 2 are common, ratio = ½
		assertTrue("Deduplicate tokens 2", matcher.matches("c c", "a b c d")); // 1+4 unique tokens, 2 are common, ratio = ⅖
		assertTrue("Deduplicate tokens 3", matcher.matches("e e", "c e e d")); // 1+3 unique tokens, 2 are common, ratio = ½
		assertFalse("Deduplicate tokens 4", matcher.matches("b a b a", "d d c c a a a a x x x x x x x x")); // 2+4 unique tokens, 2 are common, ratio = ⅓
		assertTrue("Deduplicate tokens 5", matcher.matches("b a b a", "d d c c b a a a x x x x x x x x")); // 2+5 unique tokens, 4 are common, ratio = ⁴⁄₇
	}

	@Test
	public void testIgnoreEmptyTokens() {
		CommonTokensMatcher matcher = new CommonTokensMatcher();

		matcher.setMinRatio(0.3);

		// Extra tokens from double spaces should be ignored
		assertTrue("Ignore empty tokens 1", matcher.matches("J  Str  Bra  Roy  Asi  Soc", "J  Str  Bra  Roy  Asi  Soc")); // 6+6 tokens, 6+6 are common, ratio 1
		assertFalse("Ignore empty tokens 2", matcher.matches("J  Bot", "J  Str  Bra  Roy  Asi  Soc")); // 2+6 tokens, 2 are common, ratio ¼
		assertFalse("Ignore empty tokens 3", matcher.matches("J  Str  Bra  Roy  Asi  Soc", "J  Bot")); // 6+2 tokens, 2 are common, ratio ¼
	}
}
