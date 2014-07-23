package org.kew.stringmod.lib.transformers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class YearRangeExtractorTransformerTest {

	@Test
	public void testSimple() {
		YearRangeExtractorTransformer transformer = new YearRangeExtractorTransformer();

		assertEquals("1986", transformer.transform("1986"));
		assertEquals("", transformer.transform(" XX YY "));
		assertEquals("1986", transformer.transform(" XX 1986 YY "));
		assertEquals("1986 1996 2006", transformer.transform(" A 1986 B 1996 C 2006 D "));
		assertEquals("1986 1996 2006", transformer.transform(" A 1986. B 1996, C 2006; D "));
		assertEquals("1986-1996 2006", transformer.transform(" A 1986-1996, C 2006; D "));
		assertEquals("1986-1996-2006", transformer.transform(" A 1986-1996-2006; D "));

		assertEquals("2006", transformer.transform(" A 986 B 2996 C 2006 D "));
		assertEquals("2006", transformer.transform(" A 986. B 2996, C 2006; D "));
		assertEquals("2006", transformer.transform(" A 11986 B 2996 C 2006 D "));
		assertEquals("2006", transformer.transform(" A 19861. B 2996, C 2006; D "));

		assertEquals("1986+", transformer.transform(" XX 1986+ YY "));
		assertEquals("1986-1996 2006+", transformer.transform(" A 1986-1996, C 2006+; D "));
	}
}
