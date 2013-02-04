package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class AuthorCommonTokensMatcherTest {

	@Test
	public void testMatchesExactly () {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertEquals(true, matcher.matches("Hans D.", "Hans D."));
	}

	@Test
	public void testMatchesWithpunctuation () {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertEquals(false, matcher.matches("Hans Delafontaine", "Hans De-la-fointaine"));
	}

	@Test
	public void testMatchesWithandWithoutDiacrits () {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		// diacrits are replaced with whitespace, so the following should fail:
		assertEquals(false, matcher.matches("Jaques Leblée", "Jaques Leblee"));
		// quick double-check
		assertEquals(true, matcher.matches("Jaques Leblée", "Jaques Leblée"));

	}
}
