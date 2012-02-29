package org.kew.shs.dedupl.matchers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NeverMatchingMatcherTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NeverMatchingMatcherTest(String testName){
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( NeverMatchingMatcherTest.class );
    }

	public void testNullMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches(null, null));
	}

	public void testBlankMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", ""));
	}

	public void testNullBlankMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("", null));
	}

	public void testStringMatches() {
		Matcher matcher = new NeverMatchingMatcher();
		assertFalse(matcher.matches("one", "one"));
	}
	

}