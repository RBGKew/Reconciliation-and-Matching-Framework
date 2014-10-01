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

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.kew.rmf.utils.Dictionary;

public class LevenshteinMatcherTest {

	private Dictionary dict = new TestDictionary();

	public class TestDictionary extends HashMap<String,String> implements Dictionary {
		private static final long serialVersionUID = 1L;

		public TestDictionary() {
			put("hinz", "kunz");
			put("c", "d");
		}

		@Override
		public String get(String key) {
			return super.get(key);
		}
	}

    @Test
    public void test() throws MatchException {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(2);
        assertTrue(matcher.matches("hallo", "haaallo"));
        matcher.setMaxDistance(1);
        assertFalse(matcher.matches("hallo", "haaallo"));
    }

    @Test
    public void testFalsePositives() throws IOException, MatchException {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(3);
        assertTrue(matcher.matches("hinz", "kunz"));
        matcher.setDictionary(dict);
        assertFalse(matcher.matches("hinz", "kunz"));
    }

    @Test
    public void blankMatchesBlank() throws MatchException {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        assertTrue(matcher.matches("", ""));
    }
}
