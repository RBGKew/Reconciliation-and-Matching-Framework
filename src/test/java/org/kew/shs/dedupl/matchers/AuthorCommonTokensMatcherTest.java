package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class AuthorCommonTokensMatcherTest {

	@Test
	public void testMatchesExactly () {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertTrue(matcher.matches("Hans D.", "Hans D."));
	}

	@Test
	public void testMatchesWithpunctuation () {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertFalse(matcher.matches("Hans Delafontaine", "Hans De-la-fointaine"));
	}

	@Test
	public void testMatchesWithandWithoutDiacrits () {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		// diacrits are replaced with whitespace, so the following should fail:
		assertFalse(matcher.matches("Jaques Leblée", "Jaques Leblee"));
		// quick double-check
		assertTrue(matcher.matches("Jaques Leblée", "Jaques Leblée"));
	}

}
