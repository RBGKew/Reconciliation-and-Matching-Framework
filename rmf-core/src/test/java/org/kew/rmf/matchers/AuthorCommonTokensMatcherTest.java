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

public class AuthorCommonTokensMatcherTest {

	@Test
	public void testMatchesExactly () throws Exception {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertTrue(matcher.matches("Hans D.", "Hans D."));
	}

	@Test
	public void testMatchesWithpunctuation () throws Exception {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertFalse(matcher.matches("Hans Delafontaine", "Hans De-la-fointaine"));
	}

	@Test
	public void testMatchesWithDiacrits () throws Exception {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertTrue(matcher.matches("Jaques Leblée", "Jaques Leblee"));
	}

}
