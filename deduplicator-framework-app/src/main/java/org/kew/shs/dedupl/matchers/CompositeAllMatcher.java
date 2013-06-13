package org.kew.shs.dedupl.matchers;


/**
 * This matcher is  wrapper for multiple matchers, 
 * all of which must match. 
 * @author nn00kg
 *
 */
public class CompositeAllMatcher extends CompositeMatcher{

	public boolean matches(String s1, String s2) {
		boolean matches = true;
		for (Matcher m : matchers)
			if (!m.matches(s1, s2)){
				matches = false;
				break;
			}
		return matches;
	}

}