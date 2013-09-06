package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

/*
 * This is just a best-practise chain of three transformations:
 * (1) replaces diacritic characters with their ASCII equivalent (NormaliseDiacriticsTransformer)
 * (2) replaces a (default: all the non-alphanumeric (== NON-ASCII) characters) with b (default: whitespace)
 * (3) replaces multiple whitespace occurrences with one whitespace
 * returns a trimed result
 */
@LibraryRegister(category="transformers")
public class SafeStripNonAlphaNumericsTransformer implements Transformer {

	private String a = "[^A-Za-z0-9]";
	private String b = " ";

	@Override
	public String transform(String s) {
		// normalise diacritics
		s = new NormaliseDiacritsTransformer().transform(s);
		// replace all characters that make sense with their ACII-like equivalent
		// NOTE: this is to be defined domain-specific!
		return s.replaceAll(a, b).replaceAll("\\s+", " ").trim();
	}
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}

}
