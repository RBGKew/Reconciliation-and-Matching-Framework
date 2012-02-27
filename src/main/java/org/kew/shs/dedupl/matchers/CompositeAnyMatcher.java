package org.kew.shs.dedupl.matchers;


/**
 * This matcher is  wrapper for multiple matchers, 
 * at least one of which must match. 
 * @author nn00kg
 *
 */
public class CompositeAnyMatcher extends CompositeMatcher{

	public boolean matches(String s1, String s2) {
		boolean matches = false;
		for (Matcher m : matchers)
			if (m.matches(s1, s2)){
				matches = true;
				break;
			}
		return matches;
	}

}