package org.kew.shs.dedupl.transformers;

public class SafeStripNonAlphasTransformer implements Transformer {

	/*
	 * First replaces diacritic characters with their ASCII equivalent, then removes
	 * all the non-alphabetic (== the NON-ASCII letters leaving numbers) characters.
	 */
	@Override
	public String transform(String s) {
		s = new NormaliseDiacritsTransformer().transform(s);
		s = s.replaceAll("[^\\p{ASCII}]", "");
		return s.replaceAll("[^A-Za-z]", " ").replaceAll("\\s+", " ").trim();
	}

}
