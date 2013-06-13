package org.kew.shs.dedupl.matchers;

import org.kew.shs.dedupl.transformers.ZeroToBlankTransformer;

/**
 * This matcher tests for initial substring equality between the two inputs.
 * @author nn00kg
 *
 */
public class InitialSubstringMatcher implements Matcher {

	public static int COST = 0;
	public int prefixSize;
	
	public int getCost() {
		return COST;
	}

	public boolean matches(String s1, String s2) {
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			if ((s1.length() > prefixSize) && (s2.length() > prefixSize )){
				matches=s1.substring(0, prefixSize).equals(s2.substring(0, prefixSize));
			}
		}
		return matches;
	}

	public boolean isExact() {
		return false;
	}

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