package org.kew.rmf.matchers;

import org.kew.rmf.transformers.StripNonNumericCharactersTransformer;

/**
 * This matcher tests for common tokens using only the numeric tokens in the strings supplied.
 *
 * If noNumbersRequireRestMatch == true and both strings don't contain any numbers,
 * they are matched for exact string equality.
 */
public class NumberMatcher extends CommonTokensMatcher {

	public static int COST = 5;
	public boolean noNumbersRequireRestMatch = true;

	StripNonNumericCharactersTransformer removeNumbers = new StripNonNumericCharactersTransformer();

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		String no1 = doConvert(s1);
		String no2 = doConvert(s2);
		if (noNumbersRequireRestMatch && no1.length() == 0 && no2.length() == 0) {
			return (s1.equals(s2));
		} else return super.matches(no1, no2);
	}

	private String doConvert(String s) {
		return this.removeNumbers.transform(s);
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}

	public static void main(String[] args) throws Exception {
		NumberMatcher m = new NumberMatcher();
		System.out.println(m.matches("7: 139", "7: 139 (-140)"));
	}

	public boolean isNoNumbersRequireRestMatch() {
		return noNumbersRequireRestMatch;
	}

	public void setNoNumbersRequireRestMatch(boolean noNumbersRequireRestMatch) {
		this.noNumbersRequireRestMatch = noNumbersRequireRestMatch;
	}
}
