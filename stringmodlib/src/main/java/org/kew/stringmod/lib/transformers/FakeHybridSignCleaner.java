package org.kew.stringmod.lib.transformers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * Deletes X and x that seem to be meant as hybrid signs
 *
 * X and x can be at the beginning of a string followed by a whitespace or
 * anywhere in the string if surrounded by white-spaces.
 */
@LibraryRegister(category="transformers")
public class FakeHybridSignCleaner extends A2BTransformer {

	final private String a = "^[Xx]\\s|\\s[xX]\\s";
	final private String b = " ";
	
	public String getA() {
		return this.a;
	}
	public String getB() {
		return this.b;
	}

}
