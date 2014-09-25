package org.kew.rmf.matchers;

/**
 * This matcher returns true if either of the strings is null
 * @author nn00kg
 *
 */
public class EmptyStringMatcher implements Matcher {

	public static int COST = 0;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		
		boolean match = false;
		if (s1 == null || s2 == null)
			match = true;

		return match;
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}
}
