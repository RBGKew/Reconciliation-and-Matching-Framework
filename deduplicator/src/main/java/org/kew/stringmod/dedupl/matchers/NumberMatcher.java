package org.kew.stringmod.dedupl.matchers;

import org.kew.stringmod.lib.transformers.StripNonNumericCharactersTransformer;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens using only the numeric tokens in the strings supplied.
 *
 * If noNumbersRequireRestMatch == true and both strings don't contain any numbers,
 * they are matched for exact string equality.
 */
@LibraryRegister(category="matchers")
public class NumberMatcher extends CommonTokensMatcher {

	public static int COST = 5;
	public boolean noNumbersRequireRestMatch = true;
	
	Transformer removeNumbers = new StripNonNumericCharactersTransformer();
	
	public int getCost() {
		return COST;
	}

	@Cacheable(cacheName="nctMatchCache")
	public boolean matches(String s1, String s2) throws Exception {
		if (s1 == null && s2 == null) return true;
		String no1 = doConvert(s1);
		String no2 = doConvert(s2);
		if (noNumbersRequireRestMatch && no1.length() == 0 && no2.length() == 0) {
			return (s1.equals(s2));
		} else return super.matches(no1, no2);
	}

	private String doConvert(String s) throws Exception{
		return this.removeNumbers.transform(s);
	}
	
	public boolean isExact() {
		return false;
	}
	
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