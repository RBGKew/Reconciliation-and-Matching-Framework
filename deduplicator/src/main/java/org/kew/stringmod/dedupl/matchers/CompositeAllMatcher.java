package org.kew.stringmod.dedupl.matchers;

import org.kew.stringmod.utils.LibraryRegister;


/**
 * This matcher is  wrapper for multiple matchers, 
 * all of which must match. 
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class CompositeAllMatcher extends CompositeMatcher{

	public boolean matches(String s1, String s2) throws Exception {
		boolean matches = true;
		for (Matcher m : matchers)
			if (!m.matches(s1, s2)){
				matches = false;
				break;
			}
		return matches;
	}

}