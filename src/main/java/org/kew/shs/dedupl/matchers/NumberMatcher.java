package org.kew.shs.dedupl.matchers;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens using only the numeric tokens in the strings supplied.
 * @author nn00kg
 *
 */
public class NumberMatcher extends CommonTokensMatcher {

	public static int COST = 5;
	private double minRatio=0.5;
	
	public double getMinRatio() {
		return minRatio;
	}

	public void setMinRatio(double minRatio) {
		this.minRatio = minRatio;
	}

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
				matches = super.matches(doConvert(s1),doConvert(s2));
			}
			catch (Exception e) {
				;
			}
		}
		return matches;
	}

	private String doConvert(String s){
		return s.replaceAll("[^0-9]", " ").replaceAll("\\s+", " ");
	}
	
	public boolean isExact() {
		return true;
	}
	
	public String getExecutionReport() {
		return null;
	}
	
	public static void main(String[] args) {
		NumberMatcher m = new NumberMatcher();
		System.out.println(m.matches("7: 139", "7: 139 (-140)"));
	}
	
}