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

/**
 * Abstract {@link Matcher} to split a string into delimited tokens before matching.
 * <br/>
 * By default with {@link #minLength} <code>1</code>, empty tokens are ignored.
 */
public abstract class TokeniserMatcher implements Matcher {

	private String delimiter = " ";
	protected int minLength = 1;

	protected String[] convToArray(String s) {
		if (s == null) { return new String[0]; }

		String[] a = s.split(this.delimiter, -1);

		// if delimiter is blank we want every element to be represented as one item in the array;
		// however, the first and last element swould be blank, which we correct here.
		if (this.getDelimiter().isEmpty()) a = Arrays.copyOfRange(a, 1, a.length -1);

		// remove elements that are too short
		if (minLength > 0) {
			int bi = 0;
			String[] b = new String[a.length];
			for (int ai = 0; ai < a.length; ai++) {
				if (a[ai].length() >= minLength) {
					b[bi++] = a[ai];
				}
			}
			a = Arrays.copyOf(b, bi);
		}
		return a;
	}

	public String getDelimiter() {
		return delimiter;
	}
	/**
	 * Set the delimiter.  The default is " " (a space).
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public int getMinLength() {
		return minLength;
	}
	/**
	 * Set the minimum token length.  To preserve empty tokens (caused by adjacent delimiters)
	 * set this to zero.
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
}
