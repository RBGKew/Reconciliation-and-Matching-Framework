package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.*;

import org.junit.Test;

import org.kew.shs.dedupl.util.Dictionary;

public class LevenshteinMatcherTest {

    @Test
    public void test() {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(2);
        assertTrue(matcher.matches("hallo", "haaallo"));
        matcher.setMaxDistance(1);
        assertFalse(matcher.matches("hallo", "haaallo"));
    }

    @Test
    public void testFalsePositives() {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(3);
        assertTrue(matcher.matches("hinz", "kunz"));
        Dictionary falsePositives = new Dictionary() {{ put("hinz", "kunz"); }};
        matcher.setDict(falsePositives);
        assertFalse(matcher.matches("hinz", "kunz"));
    }

}
