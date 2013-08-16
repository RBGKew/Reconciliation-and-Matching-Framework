package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SafeStripNonAlphasTransformerTest {

	@Test
	public void blank2blank () throws Exception {
		Transformer transformer = new SafeStripNonAlphasTransformer();
		assertEquals("", transformer.transform(""));
	}

	@Test
	public void aStringWithDiacritsAndNumbersAndPunctuation () throws Exception {
		Transformer transformer = new SafeStripNonAlphasTransformer();
		assertEquals("Tete a tete en", transformer.transform("Tête-à-tête en 2"));
	}

	@Test
	public void twoStringsWithHyphen () throws Exception {
		Transformer transformer = new SafeStripNonAlphasTransformer();
		assertEquals("hello kitty", transformer.transform("hello-kitty"));
	}

	@Test
	public void stringWithDotAtTheEnd () throws Exception {
		Transformer transformer = new SafeStripNonAlphasTransformer();
		assertEquals("hallo", transformer.transform("hallo."));
	}
}
