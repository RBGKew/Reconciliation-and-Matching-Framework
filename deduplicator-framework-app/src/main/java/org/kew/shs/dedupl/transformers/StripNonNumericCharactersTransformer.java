package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * This transformer strips non numeric characters
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class StripNonNumericCharactersTransformer implements Transformer{

	public String transform(String s) {
		return s.replaceAll("[^0-9]", " ").replaceAll("\\s+", " ");
	}
	
}