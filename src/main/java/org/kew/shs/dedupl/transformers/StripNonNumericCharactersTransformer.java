package org.kew.shs.dedupl.transformers;

/**
 * This transformer strips non numeric characters
 * @author nn00kg
 *
 */
public class StripNonNumericCharactersTransformer implements Transformer{

	public String transform(String s) {
		return s.replaceAll("[^0-9]", " ").replaceAll("\\s+", " ");
	}
	
}