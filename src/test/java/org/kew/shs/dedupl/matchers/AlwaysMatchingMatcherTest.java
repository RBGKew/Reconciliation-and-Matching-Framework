package org.kew.shs.dedupl.matchers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AlwaysMatchingMatcherTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AlwaysMatchingMatcherTest(String testName){
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( AlwaysMatchingMatcherTest.class );
    }

	public void testNullMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches(null, null));
	}

	public void testBlankMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("", ""));
	}

	public void testNullBlankMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("", null));
	}

	public void testStringMatches() {
		Matcher matcher = new AlwaysMatchingMatcher();
		assertTrue(matcher.matches("one", "two"));
	}
	

}