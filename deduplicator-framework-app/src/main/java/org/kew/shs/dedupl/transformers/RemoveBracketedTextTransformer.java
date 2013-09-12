package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

/*
 * Removes all text in brackets (round and square) incl. the brackets.
 *
 * TODO: not sure how that deals with nested structures, needs more testing
 */
@LibraryRegister(category="transformers")
public class RemoveBracketedTextTransformer extends A2BTransformer {
	
	final private String a = String.format("%s|%s|%s", ROUND_BRACKETS_AND_CONTENT, SQUARE_BRACKETS_AND_CONTENT, CURLY_BRACKETS_AND_CONTENT);

	public String getA() {
		return this.a;
	}

}
