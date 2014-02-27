package org.kew.stringmod.lib.transformers;

/**
 * This transformer strips non alphabetic characters
 *
 * @deprecated Only works on ASCII; use SafeStripNonAlphanumericsTransformer instead.
 */
@Deprecated
public class StripNonAlphanumericCharactersTransformer implements Transformer{

	public String transform(String s) {
		return s.replaceAll("[^A-Za-z0-9]", " ").replaceAll("\\s+", " ");
	}
	
}
