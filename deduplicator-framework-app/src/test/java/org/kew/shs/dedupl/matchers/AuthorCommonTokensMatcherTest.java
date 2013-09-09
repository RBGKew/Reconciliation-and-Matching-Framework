package org.kew.shs.dedupl.matchers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class AuthorCommonTokensMatcherTest {

	@Test
	public void testMatchesExactly () throws Exception {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertTrue(matcher.matches("Hans D.", "Hans D."));
	}

	@Test
	public void testMatchesWithpunctuation () throws Exception {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertFalse(matcher.matches("Hans Delafontaine", "Hans De-la-fointaine"));
	}

	@Test
	public void testMatchesWithDiacrits () throws Exception {
		AuthorCommonTokensMatcher matcher = new AuthorCommonTokensMatcher();
		assertTrue(matcher.matches("Jaques Leblée", "Jaques Leblee"));
	}

}
