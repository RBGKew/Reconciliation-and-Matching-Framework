package org.kew.stringmod.dedupl.matchers;

import org.kew.rmf.transformers.CapitalLettersExtractor;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens using only the capital letters in the strings supplied.
 */
public class CapitalLettersMatcher extends CommonTokensMatcher{
	
	CapitalLettersExtractor removeNonCaps = new CapitalLettersExtractor();

	@Override
	@Cacheable(cacheName="clctMatchCache")
	public boolean matches(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		this.removeNonCaps.setReplacement(this.getDelimiter());
		return super.matches(this.removeNonCaps.transform(s1), this.removeNonCaps.transform(s2));
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
		CapitalLettersMatcher m = new CapitalLettersMatcher();
		System.out.println(m.matches("Addit. Fl. Jam.", "Bull. Inst. Jamaica, Sci. Ser."));
		CommonTokensMatcher cm = new CommonTokensMatcher();
		System.out.println(cm.matches("A F J", "B I J S S"));
	}

}
