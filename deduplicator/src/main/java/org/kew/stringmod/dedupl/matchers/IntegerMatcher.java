package org.kew.stringmod.dedupl.matchers;

import org.kew.stringmod.utils.LibraryRegister;

@LibraryRegister(category="matchers")
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

}
