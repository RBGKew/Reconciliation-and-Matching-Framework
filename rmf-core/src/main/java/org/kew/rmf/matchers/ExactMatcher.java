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

/**
 * This matcher tests for equality between the two inputs (exact matches).
 * @author nn00kg
 *
 */
public class ExactMatcher implements Matcher {

	public static int COST = 0;

	private boolean nullToBlank = true;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		if (nullToBlank && s1 == null) s1 = "";
		if (nullToBlank && s2 == null) s2 = "";

		return s1.equals(s2);
	}

	@Override
	public boolean isExact() {
		return true;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}

	/* ••• Getters and setters ••• */
	public boolean isNullToBlank() {
		return nullToBlank;
	}
	public void setNullToBlank(boolean nullToBlank) {
		this.nullToBlank = nullToBlank;
	}
}
