package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class FakeHybridSignCleaner implements Transformer {

	/*
	 * Replaces X and x that seem to be meant as hybrid signs
	 */
	@Override
	public String transform(String s) {
		return s.replaceAll("^[Xx]\\s|\\s[xX]\\s", " ").replaceAll("\\s+",  " ").trim();
	}

}
