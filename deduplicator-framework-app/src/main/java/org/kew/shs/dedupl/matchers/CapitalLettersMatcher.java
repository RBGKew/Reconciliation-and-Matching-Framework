package org.kew.shs.dedupl.matchers;

import org.kew.shs.dedupl.util.LibraryRegister;

import com.googlecode.ehcache.annotations.Cacheable;




/**
 * This matcher tests for common tokens using only the capital letters in the strings supplied.
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class CapitalLettersMatcher extends CommonTokensMatcher{

	public static int COST = 5;

	public CapitalLettersMatcher () {
		super.setMinRatio(0.5); // that's the only way I can imagine to overwrite commonTokenMatcher's default
	}

	public void setMinRatio(double minRatio) {
		super.setMinRatio(minRatio);
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
