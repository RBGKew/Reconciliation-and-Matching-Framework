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

import org.apache.commons.lang.StringUtils;

/**
 * This matcher tests for equality between the two inputs (exact matches).
 * @author nn00kg
 *
 */
public class ExactMatcher implements Matcher {

	public static int COST = 0;
	protected boolean blanksMatch = true;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		//if (s1 == null && s2 == null && this.blanksMatch) {
		if (this.blanksMatch && (StringUtils.isBlank(s1) || StringUtils.isBlank(s2))) {
			matches = true;
		} else{
			try{
				matches = s1.equals(s2);
			}
			catch (Exception e) {
				;
			}
		}
		return matches;
	}

	@Override
	public boolean isExact() {
		return true;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}
	
	public void setBlanksMatch(boolean blanksMatch) {
		this.blanksMatch = blanksMatch;
	}
}
