package org.kew.shs.dedupl.matchers;

import org.kew.shs.dedupl.transformers.CapitalLettersTransformer;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

import com.googlecode.ehcache.annotations.Cacheable;


/**
 * This matcher tests for common tokens using only the capital letters in the strings supplied.
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class CapitalLettersMatcher extends CommonTokensMatcher{
	
	Transformer removeNonCaps = new CapitalLettersTransformer();

	@Cacheable(cacheName="clctMatchCache")
	public boolean matches(String s1, String s2) throws Exception {
		if (s1 == null && s2 == null) return true;
		return super.matches(this.removeNonCaps.transform(s1), this.removeNonCaps.transform(s2));
	}

	public boolean isExact() {
		return false;
	}

	public String getExecutionReport() {
		return null;
	}

	public static void main(String[] args) throws Exception {
		CapitalLettersMatcher m = new CapitalLettersMatcher();
		System.out.println(m.matches("Addit. Fl. Jam.", "Bull. Inst. Jamaica, Sci. Ser."));
		CommonTokensMatcher cm = new CommonTokensMatcher();
		System.out.println(cm.matches("A F J", "B I J S S"));
	}

}
