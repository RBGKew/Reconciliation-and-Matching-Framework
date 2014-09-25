package org.kew.rmf.matchers;

/**
 * This matcher returns false in all cases.
 * @author nn00kg
 *
 */
public class NeverMatchingMatcher implements Matcher {

	public static int COST = 0;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		return false;
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