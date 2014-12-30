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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokeniserMatcherTest extends TokeniserMatcher {
	private static final Logger logger = LoggerFactory.getLogger(TokeniserMatcherTest.class);

	/**
	 * Test that various delimiters and minimum lengths work as expected.
	 */
	@Test
	public void testConvToArray() {
		String[] r;

		// Defaults (set to ensure test is OK).
		setDelimiter(" ");
		setMinLength(1);

		// Default delimiter (" ") and min length (1)
		r = convToArray("AA BBB CC");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(3));

		r = convToArray("AA  BBB   CC    ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(3));

		// Min length 0, so blank tokens should be included, including those at the end
		setMinLength(0);
		r = convToArray("AA  BBB   CC    ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(10));

		// Change delimiter
		setDelimiter("B");
		r = convToArray("AA  BBB   CC    ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(4));

		setDelimiter("BBB");
		r = convToArray("AA  BBB   CC    ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(2));

		setDelimiter("A");
		r = convToArray("AA  BBB   CC    ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(3));
	}

	@Test
	public void testConvEmptyToArray() {
		String[] r;

		// Defaults
		setDelimiter(" ");
		setMinLength(1);

		// Default delimiter (" ") and min length (1)
		r = convToArray("");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(0));

		r = convToArray("   ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(0));

		r = convToArray(null);
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(0));

		// Min length 0, so blank tokens should be included.
		setMinLength(0);
		r = convToArray("");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(1));

		r = convToArray("   ");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(4));

		r = convToArray(null);
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(0));
	}

	@Test
	public void testBlankDelimiter() {
		String[] r;

		setDelimiter("");
		setMinLength(1);

		// Blank delimiter ("") and min length 1
		r = convToArray("ABCD");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(4));

		// Min length 0
		setMinLength(0);
		r = convToArray("ABCD");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(4));

		// Min length 2
		setMinLength(2);
		r = convToArray("ABCD");
		logger.trace("{} tokens; {} {}", r.length, (Object)r);
		assertThat("Wrong number of tokens", r.length, CoreMatchers.equalTo(0));
	}

	@Override public boolean matches(String s1, String s2) throws MatchException { fail("Not tested here"); return false; }
	@Override public boolean isExact() { fail("Not tested here"); return false; }
	@Override public int getCost() { fail("Not tested here"); return 0; }
	@Override public String getExecutionReport() { fail("Not tested here"); return null; }
}
