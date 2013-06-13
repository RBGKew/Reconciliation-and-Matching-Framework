package org.kew.shs.dedupl.transformers;

/**
 * This transformer strips non alphabetic characters
 * @author nn00kg
 *
 */
public class StripNonAlphanumericCharactersTransformer implements Transformer{

	public String transform(String s) {
		return s.replaceAll("[^A-Za-z0-9]", " ").replaceAll("\\s+", " ");
	}
	
}
