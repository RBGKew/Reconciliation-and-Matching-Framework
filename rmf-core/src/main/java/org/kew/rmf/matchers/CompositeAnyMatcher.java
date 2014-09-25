package org.kew.rmf.matchers;

/**
 * This matcher is  wrapper for multiple matchers, 
 * at least one of which must match. 
 * @author nn00kg
 *
 */
public class CompositeAnyMatcher extends CompositeMatcher{

	@Override
	public boolean matches(String s1, String s2) throws MatchException {
		boolean matches = false;
		for (Matcher m : matchers)
			if (m.matches(s1, s2)){
				matches = true;
				break;
			}
		return matches;
	}
}
