package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.*;

import org.junit.Test;

public class LevenshteinMatcherTest {

    @Test
    public void test() {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(2);
        assertTrue(matcher.matches("hallo", "haaallo"));
        matcher.setMaxDistance(1);
        assertFalse(matcher.matches("hallo", "haaallo"));
    }

}
