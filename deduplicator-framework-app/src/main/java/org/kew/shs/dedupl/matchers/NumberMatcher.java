package org.kew.shs.dedupl.matchers;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens using only the numeric tokens in the strings supplied.
 * @author nn00kg
 *
 */
public class NumberMatcher extends CommonTokensMatcher {

	public static int COST = 5;
	public boolean noNumbersRequireRestMatch = true;
	
	public int getCost() {
		return COST;
	}

	@Cacheable(cacheName="nctMatchCache")
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			try{
				String no1 = doConvert(s1);
				String no2 = doConvert(s2);
				if (noNumbersRequireRestMatch && no1.length() == 0 && no2.length() == 0) {
					return (s1.equals(s2));
				}
				matches = super.matches(no1, no2);
			}
			catch (Exception e) {
				;
			}
		}
		return matches;
	}

	private String doConvert(String s){
		return s.replaceAll("[^0-9]", " ").replaceAll("\\s+", " ").trim();
	}
	
	public boolean isExact() {
		return false;
	}
	
	public String getExecutionReport() {
		return null;
	}
	
	public static void main(String[] args) {
		NumberMatcher m = new NumberMatcher();
		System.out.println(m.matches("7: 139", "7: 139 (-140)"));
	}
	
}