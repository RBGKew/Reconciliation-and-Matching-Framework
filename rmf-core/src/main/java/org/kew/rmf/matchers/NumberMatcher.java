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

import org.kew.rmf.transformers.StripNonNumericCharactersTransformer;

/**
 * This matcher tests for common tokens using only the numeric tokens in the strings supplied.
 *
 * If noNumbersRequireRestMatch == true and both strings don't contain any numbers,
 * they are matched for exact string equality.
 */
public class NumberMatcher extends CommonTokensMatcher {

	public static int COST = 5;
	public boolean noNumbersRequireRestMatch = true;

	StripNonNumericCharactersTransformer removeNumbers = new StripNonNumericCharactersTransformer();

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		String no1 = removeNumbers.transform(s1);
		String no2 = removeNumbers.transform(s2);
		if (noNumbersRequireRestMatch && no1.length() == 0 && no2.length() == 0) {
			return (s1.equals(s2));
		}
		else {
			return super.matches(no1, no2);
		}
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}

	public boolean isNoNumbersRequireRestMatch() {
		return noNumbersRequireRestMatch;
	}
	public void setNoNumbersRequireRestMatch(boolean noNumbersRequireRestMatch) {
		this.noNumbersRequireRestMatch = noNumbersRequireRestMatch;
	}
}
