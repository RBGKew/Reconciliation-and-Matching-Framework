package org.kew.rmf.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CommonTokensMatcherTest {

    @Test
    public void testMatchesExactly () throws Exception {
        Matcher matcher = new CommonTokensMatcher();
        assertTrue(matcher.matches("abc 123!", "abc 123!"));
    }

    @Test
    public void testCommonTokens () throws Exception {
        CommonTokensMatcher matcher = new CommonTokensMatcher();
        // default acceptance is a ratio of 0.5
        assertTrue(matcher.matches("first sec third", "first sec diff"));
        assertFalse(matcher.matches("first sec third", "first diff diff"));
        matcher.setMinRatio(0.2);
        assertTrue(matcher.matches("first sec third", "first diff diff"));
    }

    @Test
    public void testDelimiter() throws Exception {
        CommonTokensMatcher matcher = new CommonTokensMatcher();
        assertFalse(matcher.matches("USA", "U.S.A."));
        matcher.setDelimiter("");
        assertTrue(matcher.matches("USA", "U.S.A."));
        assertTrue(matcher.matches("abc", "abcd"));
    }

    @Test
    public void testVaryingTokenNumbers() throws Exception {
        CommonTokensMatcher matcher = new CommonTokensMatcher();
        matcher.setMinRatio(1);
        assertTrue(matcher.matches("a b c", "a b c"));
        matcher.setMinRatio(0.5);
        // ratio == 1 still matches
        assertTrue(matcher.matches("a b c", "a b c"));
        // common == [a, b], numCommon == 2, unique == [a, b, c], numUnique == 3, ratio == 2/3 == 0.6666
        assertTrue(matcher.matches("a b", "a b c"));
        // common == [a], numCommon == 1, unique == [a, b, c], numUnique == 3, ratio == 1/3 == 0.3333
        assertFalse(matcher.matches("a", "a b c"));

        // weird behaviour for the following two examples
        // common == [a, a], numCommon == 2, unique == [a, b, c], numUnique == 3, ratio == 2/3 == 0.6666
        assertTrue(matcher.matches("a a", "a b c"));
        // common == [a, a], numCommon == 2, unique == [a, b, c], numUnique == 3, ratio == 2/3 == 0.6666
        assertTrue(matcher.matches("a a", "a b a c"));

        // more intuitive?:
        matcher.setUniqueCommonTokens(true);
        // common == [a], numCommon == 2, unique == [a, b, c], numUnique == 3, ratio == 2/3 == 0.3333
        assertFalse(matcher.matches("a a", "a b c"));


    }
}
