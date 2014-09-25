package org.kew.rmf.matchers;

/**
 * This matcher is  wrapper for multiple matchers, 
 * all of which must match. 
 * @author nn00kg
 *
 */
public class CompositeAllMatcher extends CompositeMatcher{

	@Override
	public boolean matches(String s1, String s2) throws MatchException {
		boolean matches = true;
		for (Matcher m : matchers)
			if (!m.matches(s1, s2)){
				matches = false;
				break;
			}
		return matches;
	}
}
