package org.kew.shs.dedupl.matchers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ExactMatchingMatcherTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ExactMatchingMatcherTest(String testName){
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( ExactMatchingMatcherTest.class );
    }

	public void testNullMatches() {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches(null, null));
	}

	public void testBlankMatches() {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("", ""));
	}

	public void testNullBlankMatches() {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("", null));
		assertFalse(matcher.matches(null,""));
	}

	public void testStringMatches() {
		Matcher matcher = new ExactMatcher();
		assertTrue(matcher.matches("one", "one"));
	}
	
	public void testStringCaseMatches() {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "One"));
	}

	public void testStringTrimMatches() {
		Matcher matcher = new ExactMatcher();
		assertFalse(matcher.matches("one", "one "));
	}

}