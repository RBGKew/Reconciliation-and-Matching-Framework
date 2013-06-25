package org.kew.shs.dedupl.transformers;

import java.util.HashMap;
import java.util.Map;

public class SafeStripNonAlphaNumericsTransformer implements Transformer {

	static Map<String,String> ADDITIONAL_REPLACEMENTS = new HashMap<String,String>() {{
		put("Ø", "O");
		put("ø", "o");
		put("č|ĉ", "c");
		put("Ł", "L");
		put("ł", "l");
		put("—", "-");
	}};
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
		for (String key:ADDITIONAL_REPLACEMENTS.keySet()) {
			s = s.replaceAll(key, ADDITIONAL_REPLACEMENTS.get(key));
		}
		s = s.replaceAll("[^\\p{ASCII}]", "");
		return s.replaceAll("[^A-Za-z0-9]", " ").replaceAll("\\s+", " ").trim();
	}

}
