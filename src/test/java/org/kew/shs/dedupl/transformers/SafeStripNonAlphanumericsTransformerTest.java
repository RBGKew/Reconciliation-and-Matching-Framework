package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SafeStripNonAlphanumericsTransformerTest {

	@Test
	public void blank2blank () {
		Transformer transformer = new SafeStripNonAlphanumericsTransformer();
		assertEquals("", transformer.transform(""));
	}

	@Test
	public void aStringWithDiacritsAndNumbersAndPunctuation () {
		Transformer transformer = new SafeStripNonAlphanumericsTransformer();
		assertEquals("Tete a tete en 2", transformer.transform("Tête-à-tête en 2"));
	}

}
