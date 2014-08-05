package org.kew.stringmod.dedupl.matchers;

/**
 * A simple number matcher that compares two Integers and accepts a maximal difference of
 * `maxDiff` to match.
 *
 */
public class IntegerMatcher implements Matcher {

	public static int COST = 1;
	private int maxDiff = 5;

	@Override
	public boolean matches(String s1, String s2) {
		return Math.abs((Integer.parseInt(s1) - Integer.parseInt(s2))) <= this.maxDiff;
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public String getExecutionReport() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaxDiff() {
		return maxDiff;
	}

	public void setMaxDiff(int maxDiff) {
		this.maxDiff = maxDiff;
	}

}
