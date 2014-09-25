package org.kew.rmf.matchers;

/**
 * This matcher returns true in all cases.
 * @author nn00kg
 */
public class AlwaysMatchingMatcher implements Matcher {

	public static int COST = 0;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		return true;
	}

	@Override
	public boolean isExact() {
		return true;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}
}
