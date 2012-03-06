package org.kew.shs.dedupl.matchers;

import com.googlecode.ehcache.annotations.Cacheable;




/**
 * This matcher tests for common tokens using only the capital letters in the strings supplied.
 * @author nn00kg
 *
 */
public class CapitalLettersMatcher extends CommonTokensMatcher{

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

	@Cacheable(cacheName="clctMatchCache")
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			try{
				s1 = s1.replaceAll("[^A-Z]", " ").replaceAll("\\s+", " ");
				s2 = s2.replaceAll("[^A-Z]", " ").replaceAll("\\s+", " ");
				matches = super.matches(s1,s2);
			}
			catch (Exception e) {
				;
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
	
	public static void main(String[] args) {
		CapitalLettersMatcher m = new CapitalLettersMatcher();
		System.out.println(m.matches("Addit. Fl. Jam.", "Bull. Inst. Jamaica, Sci. Ser."));
		CommonTokensMatcher cm = new CommonTokensMatcher();
		System.out.println(cm.matches("A F J", "B I J S S"));
	}
	
}