package org.kew.shs.dedupl.transformers;

public class SafeStripNonAlphanumericsTransformer implements Transformer {

	/*
	 * First replaces diacritic characters with their ASCII equivalent, then removes
	 * all the non-alphanumeric (== NON-ASCII) characters.
	 */
	@Override
	public String transform(String s) {
		s = new NormaliseDiacritsTransformer().transform(s);
		return new StripNonAlphanumericCharactersTransformer().transform(s);
	}

}
