package org.kew.stringmod.lib.transformers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer strips non numeric characters
 */
@LibraryRegister(category="transformers")
public class StripNonNumericCharactersTransformer extends A2BTransformer {
	
	final private String a = "[^0-9]";
	final private String b = " ";

	@Override
	public String getA() {
		return this.a;
	}

	@Override
	public String getB() {
		return this.b;
	}
}