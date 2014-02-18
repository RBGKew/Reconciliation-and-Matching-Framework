package org.kew.stringmod.dedupl.matchers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * This matcher returns true in all cases.
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class AlwaysMatchingMatcher implements Matcher {

	public static int COST = 0;
	
	public int getCost() {
		return COST;
	}

	public boolean matches(String s1, String s2) {
		return true;
	}

	public boolean isExact() {
		return true;
	}

	public String getExecutionReport() {
		return null;
	}
	
}