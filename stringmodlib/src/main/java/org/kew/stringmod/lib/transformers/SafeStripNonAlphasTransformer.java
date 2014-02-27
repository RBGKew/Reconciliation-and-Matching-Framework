package org.kew.stringmod.lib.transformers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * This is just a best-practise chain of three transformations:
 * (1) replaces diacritic characters with their ASCII equivalent (NormaliseDiacriticsTransformer)
 * (2) replaces all non-ASCII characters with `b` (default: whitespace)
 * (3) replaces multiple whitespace occurrences with one whitespace
 * returns a trimmed result
 */
@LibraryRegister(category="transformers")
public class SafeStripNonAlphasTransformer implements Transformer {

	private final String a = "[^A-Za-z]";
	private String replaceWith = " ";

	@Override
	public String transform(String s) {
		s = new NormaliseDiacritsTransformer().transform(s);
		return s.replaceAll(a, replaceWith).replaceAll("\\s+", " ").trim();
	}

	public String getA() {
		return a;
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}
	
}
