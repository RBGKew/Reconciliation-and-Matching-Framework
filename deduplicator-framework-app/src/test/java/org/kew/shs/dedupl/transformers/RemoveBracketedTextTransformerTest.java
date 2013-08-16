package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RemoveBracketedTextTransformerTest {

	@Test
	public void blank2blank () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("", transformer.transform(""));
	}

	@Test
	public void leaveOtherStuff () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("iuoiy*&^%$--tfghvbhjk", transformer.transform("iuoiy*&^%$--tfghvbhjk"));
	}

	@Test
	public void removeRoundBracketsWithText () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("text", transformer.transform("text (text)"));
	}
	@Test
	public void removeRoundBracketsWithSpecificTextInRoundBrackets () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("Prodr.", transformer.transform("Prodr. (DC.)"));
	}
	@Test
	public void removeRoundBracketsWithSpecificTextInSquaredBrackets () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("Bot. Voy. Herald", transformer.transform("Bot. Voy. Herald [Seemann]"));
	}

	@Test
	public void removeSquareBracketsWithText () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("text", transformer.transform("[text] text"));
	}

	@Test
	public void removeSquareBracketsWithoutText () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("text", transformer.transform("text[]"));
	}

	@Test
	public void removeRoundBracketsWithoutText () throws Exception {
		Transformer transformer = new RemoveBracketedTextTransformer();
		assertEquals("text", transformer.transform("te()xt"));
	}

}
