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
