package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class FakeHybridSignCleaner implements Transformer {

	/*
	 * First replaces diacritic characters with their ASCII equivalent, then removes
	 * all the non-alphabetic (== the NON-ASCII letters leaving numbers) characters.
	 */
	@Override
	public String transform(String s) {
		return s.replaceAll("^X ", "");
	}

}
