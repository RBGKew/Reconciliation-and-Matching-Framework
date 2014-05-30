package org.kew.stringmod.lib.transformers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * Removes all text in brackets (round, square and curly) incl. the brackets.
 *
 * TODO: not sure how that deals with nested structures, needs more testing
 */
@LibraryRegister(category="transformers")
public class RemoveBracketedTextTransformer extends A2BTransformer {
	
	final private String a = String.format("%s|%s|%s", ROUND_BRACKETS_AND_CONTENT, SQUARE_BRACKETS_AND_CONTENT, CURLY_BRACKETS_AND_CONTENT);

	@Override
	public String getA() {
		return this.a;
	}
}
