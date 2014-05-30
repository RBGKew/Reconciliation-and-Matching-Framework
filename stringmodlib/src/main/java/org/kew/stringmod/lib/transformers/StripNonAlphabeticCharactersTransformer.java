package org.kew.stringmod.lib.transformers;

/**
 * This transformer strips non alphabetic characters
 *
 * @deprecated Only works on ASCII; use SafeStripNonAlphasTransformer instead.
 */
@Deprecated
public class StripNonAlphabeticCharactersTransformer implements Transformer{

	@Override
	public String transform(String s) {
		return s.replaceAll("[^A-Za-z]", " ").replaceAll("\\s+", " ");
	}

}
