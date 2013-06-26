package org.kew.shs.dedupl.transformers;

import java.util.HashMap;
import java.util.Map;

public class SafeStripNonAlphaNumericsTransformer implements Transformer {

	/*
	 * First replaces diacritic characters with their ASCII equivalent, then removes
	 * all the non-alphanumeric (== NON-ASCII) characters.
	 */
	@Override
	public String transform(String s) {
		// normalise diacritics
		s = new NormaliseDiacritsTransformer().transform(s);
		// replace all characters that make sense with their ACII-like equivalent
		// NOTE: this is to be defined domain-specific!
		return s.replaceAll("[^A-Za-z0-9]", " ").replaceAll("\\s+", " ").trim();
	}

}
