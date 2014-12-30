/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.matchers;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Two strings match with this Matcher if they have more than {@link #minRatio} tokens in common.
 * <br/>
 * By default the ratio is calculated by "pairing off" common tokens. "A B" and "A B B" have two pairs (4 tokens)
 * in common, out of 5 tokens.  The ratio is ⅘.
 * <br/>
 * Setting {@link #setDeduplicateTokens(boolean)} to <code>true</code> causes duplicates to be ignored, so
 * "A A B C" and "A B B D" have two pairs (4 tokens) out of 3+3 deduplicated tokens, so the ration is ⅔.
 */
public class CommonTokensMatcher extends TokeniserMatcher {
	private static Logger logger = LoggerFactory.getLogger(CommonTokensMatcher.class);

	public static int COST = 5;
	protected double minRatio = 0.5;
	private boolean deduplicateTokens = false;

	@Override
	public boolean matches(String s1, String s2) {
		logger.trace("s1 {}; s2 {}", s1, s2);
		if (s1 == null && s2 == null) return true;
		String[] a1 = convToArray(s1);
		String[] a2 = convToArray(s2);
		return calculateTokensInCommon(a1,a2);
	}

	protected boolean calculateTokensInCommon(String[] s1, String[] s2) {

		//logger.info("Comparing {} and {}", s1, s2);
		// Sort both arrays.
		Arrays.sort(s1); // Complexity less than n₁·ln n₁
		Arrays.sort(s2); // Complexity less than n₂·ln n₂

		//logger.info("Sorted {} and {}", s1, s2);

		// Set an index s1i to s1 to 0
		int s1i = 0;
		int s2i = 0;

		int matches = 0;
		int c;

		int duplicates = 0;

		// With each element of s1, go through s2 (from where we left off last time) to find that element.
		while (true) { // Complexity n₁ + n₂ ?
			// Stop if we've reached the end of either array
			if (s1i >= s1.length) {
				if (deduplicateTokens) {
					while (s2i+1 < s2.length) {
						if (s2[s2i].equals(s2[s2i+1])) {
							duplicates++;
							//logger.info("Skipping s2[{}] {}, {} duplicates", s2i, s2[s2i], duplicates);
						}
						s2i++;
					}
				}

				break;
			}
			if (s2i >= s2.length) {
				if (deduplicateTokens) {
					while (s1i+1 < s1.length) {
						if (s1[s1i].equals(s1[s1i+1])) {
							duplicates++;
							//logger.info("Skipping s1[{}] {}, {} duplicates", s1i, s1[s1i], duplicates);
						}
						s1i++;
					}
				}

				break;
			}

			// Skip to the last of successive duplicates if required
			if (deduplicateTokens) {
				while (s1i+1 < s1.length && s1[s1i].equals(s1[s1i+1])) {
					duplicates++;
					//logger.info("Skipping s1[{}] {}, {} duplicates", s1i, s1[s1i], duplicates);
					s1i++;
				}
				while (s2i+1 < s2.length && s2[s2i].equals(s2[s2i+1])) {
					duplicates++;
					//logger.info("Skipping s2[{}] {}, {} duplicates", s2i, s2[s2i], duplicates);
					s2i++;
				}
			}

			c = s1[s1i].compareTo(s2[s2i]);
			//logger.info("Comparing s1[{}] {} with s2[{}] {}, result {}", s1i, s1[s1i], s2i, s2[s2i], c);

			if (c == 0) {
				matches += 2;
				// If they're the same increment both counters.
				s1i++;
				s2i++;
			}
			else if (c > 0) {
				// If s1's element is larger continue with the next in s2
				s2i++;
			}
			else {
				// If s1's element is smaller continue with the next s1
				s1i++;
			}
		}

		int combinedLength = s1.length + s2.length - duplicates;

		double ratio = ((double)matches) / ((double)combinedLength);

		logger.trace("{} matches {}? Counted {} paired tokens from {} combined (deduplicated) length, ratio {}", s1, s2, matches, combinedLength, ratio);

		return ratio >= this.minRatio;
	}

	public double getMinRatio() {
		return this.minRatio;
	}
	/**
	 * The minimum ratio for the match to be true.
	 */
	public void setMinRatio(double minRatio) {
		this.minRatio = minRatio;
	}

	public boolean isDeduplicateTokens() {
		return deduplicateTokens;
	}
	/**
	 * If true, each set of tokens is deduplicated so repeats are only counted once (as either in a matched pair, or as part of the total number of tokens).
	 */
	public void setDeduplicateTokens(boolean deduplicateTokens) {
		this.deduplicateTokens = deduplicateTokens;
	}

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}
}
