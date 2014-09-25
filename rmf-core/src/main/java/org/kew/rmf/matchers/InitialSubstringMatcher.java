package org.kew.rmf.matchers;

import org.kew.rmf.transformers.ZeroToBlankTransformer;

/**
 * This matcher tests for initial substring equality between the two inputs.
 * @author nn00kg
 *
 */
public class InitialSubstringMatcher implements Matcher {

	public static int COST = 0;
	public int prefixSize = 3;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if ((s1.length() > prefixSize) && (s2.length() > prefixSize )){
			return s1.substring(0, prefixSize).equals(s2.substring(0, prefixSize));
		}
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

	public int getPrefixSize() {
		return prefixSize;
	}

	public void setPrefixSize(int prefixSize) {
		this.prefixSize = prefixSize;
	}

	public static void main(String[] args) {
		ZeroToBlankTransformer z = new ZeroToBlankTransformer();
		
		InitialSubstringMatcher ism = new InitialSubstringMatcher();
		ism.setPrefixSize(3);
		System.out.println(ism.matches(z.transform("1794"), z.transform("1802")));
	}
}
