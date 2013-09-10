package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * This transformer strips non numeric characters
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class StripNonNumericCharactersTransformer extends A2BTransformer {
	
	final private String a = "[^0-9]";
	final private String b = " ";
	
	public String getA() {
		return this.a;
	}
	public String getB() {
		return this.b;
	}
}