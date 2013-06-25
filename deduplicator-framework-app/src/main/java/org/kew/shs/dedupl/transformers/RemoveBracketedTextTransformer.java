package org.kew.shs.dedupl.transformers;

public class RemoveBracketedTextTransformer implements Transformer {

	/*
	 * Removes all text in brackets (round and square) incl. the brackets.
	 */
	@Override
	public String transform(String s) {
		return s.replaceAll("[\\[|(].*[\\]|)]", "").trim();
	}

}
