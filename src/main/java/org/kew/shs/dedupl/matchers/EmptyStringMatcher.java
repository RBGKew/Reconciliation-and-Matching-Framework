package org.kew.shs.dedupl.matchers;

/**
 * This matcher returns false in all cases.
 * @author nn00kg
 *
 */
public class EmptyStringMatcher implements Matcher {

	public static int COST = 0;
	
	public int getCost() {
		return COST;
	}

	public boolean matches(String s1, String s2) {
		
		boolean match = false;
		if (s1 == null || s2 == null)
			match = true;

		return match;
	}

	public boolean isExact() {
		return false;
	}

	public String getExecutionReport() {
		return null;
	}
	
}