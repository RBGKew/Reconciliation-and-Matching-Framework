package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class NormaliseDiacritsTransformerTest {

	@Test
	public void blankToblank () {
		Transformer transformer = new NormaliseDiacritsTransformer();
		assertEquals("", transformer.transform(""));
	}

	@Test
	public void frenchWordToAscii () {
		Transformer transformer = new NormaliseDiacritsTransformer();
		assertEquals("Tete-a-tete", transformer.transform("Tête-à-tête"));
	}

	@Test
	public void wordWithHybridSignToAscii () {
		Transformer transformer = new NormaliseDiacritsTransformer();
		assertEquals(" hybrid-species", transformer.transform("× hybrid-species"));
	}

	@Test
	public void numericsAresafe () {
		Transformer transformer = new NormaliseDiacritsTransformer();
		assertEquals("text 123 text 4.5", transformer.transform("text 123 text 4.5"));
	}

}
