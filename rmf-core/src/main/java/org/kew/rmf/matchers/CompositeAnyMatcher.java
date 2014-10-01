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
 * This matcher is  wrapper for multiple matchers, 
 * at least one of which must match. 
 * @author nn00kg
 *
 */
public class CompositeAnyMatcher extends CompositeMatcher{

	@Override
	public boolean matches(String s1, String s2) throws MatchException {
		boolean matches = false;
		for (Matcher m : matchers)
			if (m.matches(s1, s2)){
				matches = true;
				break;
			}
		return matches;
	}
}
