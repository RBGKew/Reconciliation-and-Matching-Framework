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

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This matcher tests for a number appearing in a number range, e.g. "34" ? "12-18 30-36" is true.
 */
public class NumberInRangeMatcher implements Matcher {

	private static final String rangesRegex = "\\b(\\d+)([-–—+]?|\\b)(\\d*\\b?)";
	private static final Pattern rangesPattern = Pattern.compile(rangesRegex);

	@Override
	public boolean matches(String s1, String s2) {
		// Search for ranges
		if (s1 == null && s2 == null) return true;
		if (s1 == null || s1.length() == 0) return false;

		int n;
		try {
			n = Integer.parseInt(s1);
		}
		catch (NumberFormatException e) {
			return false;
		}

		// Find ranges
		for (int[] range : findRanges(s2)) {
			if (range[0] <= n && n <= range[1]) {
				return true;
			}
		}

		return false;
	}

	private ArrayList<int[]> findRanges(String s) {
		ArrayList<int[]> ranges = new ArrayList<>();
		if (s != null) {
			java.util.regex.Matcher m = rangesPattern.matcher(s);
			while (m.find()) {
				int[] r = new int[2];
				try {
					// Start of range
					r[0] = Integer.parseInt(m.group(1));

					if (m.group(3).isEmpty()) {
						// + and no end means open ended
						if ("+".equals(m.group(2))) {
							r[1] = Integer.MAX_VALUE;
						}
						else {
							r[1] = r[0];
						}
					}
					else {
						r[1] = Integer.parseInt(m.group(3));
					}

					// Check for ranges like "32-5", "1843-88", which really mean "32-35", "1843-1888"
					if (r[1] < r[0]) {
						int magEnd = r[1] / 10 + 1;
						r[1] = (r[0] / magEnd) * magEnd + r[1];
					}
				}
				catch (NumberFormatException e) {
					continue;
				}
				ranges.add(r);
			}
		}
		return ranges;
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}

	@Override
	public int getCost() {
		return 3;
	}
}
