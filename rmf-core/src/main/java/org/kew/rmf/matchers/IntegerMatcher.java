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
 * A simple number matcher that compares two Integers and accepts a maximal difference of
 * `maxDiff` to match.
 *
 */
public class IntegerMatcher implements Matcher {

	public static int COST = 1;
	private int maxDiff = 5;

	@Override
	public boolean matches(String s1, String s2) {
		return Math.abs((Integer.parseInt(s1) - Integer.parseInt(s2))) <= this.maxDiff;
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public String getExecutionReport() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaxDiff() {
		return maxDiff;
	}

	public void setMaxDiff(int maxDiff) {
		this.maxDiff = maxDiff;
	}

}
