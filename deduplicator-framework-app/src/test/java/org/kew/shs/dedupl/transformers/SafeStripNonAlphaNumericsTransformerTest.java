package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SafeStripNonAlphaNumericsTransformerTest {

	@Test
	public void blank2blank () {
		Transformer transformer = new SafeStripNonAlphaNumericsTransformer();
		assertEquals("", transformer.transform(""));
	}

	@Test
	public void wordWithHybridSignToAscii () {
		Transformer transformer = new SafeStripNonAlphaNumericsTransformer();
		assertEquals("hybrid species", transformer.transform("× hybrid-species"));
	}

	@Test
	public void numericsAresafe () {
		Transformer transformer = new SafeStripNonAlphaNumericsTransformer();
		assertEquals("text 123 text 4 5", transformer.transform("text 123 text 4.5"));
	}

	@Test
	public void withHyphenAndNumbersAndPunctuation () {
		Transformer transformer = new SafeStripNonAlphaNumericsTransformer();
		assertEquals("Tete a tete en 2", transformer.transform("Tête-à-tête en 2"));
	}
	
	@Test
	public void replaceEmDashes () {
		Transformer transformer = new SafeStripNonAlphaNumericsTransformer();
		assertEquals("hello emdash", transformer.transform("hello—emdash"));
	}

	@Test
	public void testDifferentDiacritics () {
		Transformer transformer = new SafeStripNonAlphaNumericsTransformer();
		assertEquals("Sorensen", transformer.transform("Sørensen"));
		assertEquals("Muller", transformer.transform("Müller"));
		assertEquals("Moller", transformer.transform("Möller"));
		assertEquals("E Desv", transformer.transform("É.Desv."));
		assertEquals("c or c", transformer.transform("č or ĉ"));
		assertEquals("L and l", transformer.transform("Ł and ł"));
	}

}
