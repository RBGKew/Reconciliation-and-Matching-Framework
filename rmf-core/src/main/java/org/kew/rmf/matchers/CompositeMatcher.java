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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This abstract class defines a composite matcher, a wrapper for multiple matchers
 * @author nn00kg
 *
 */
public abstract class CompositeMatcher implements Matcher{

	protected List<Matcher> matchers;

	@Override
	public abstract boolean matches(String s1, String s2) throws MatchException;

	public List<Matcher> getMatchers() {
		return matchers;
	}

	public void setMatchers(List<Matcher> matchers) {
		this.matchers = matchers;
		Collections.sort(matchers,  new Comparator<Matcher>() {
			@Override
	        public int compare(Matcher m1,Matcher m2) {
	        	return Integer.valueOf(m1.getCost()).compareTo(Integer.valueOf(m2.getCost()));
	        }
	    });
	}

	@Override
	public boolean isExact() {
		boolean exact = true;
		for (Matcher m : matchers){
			exact = m.isExact();
			if (!exact)
				break;
		}
		return exact;
	}

	@Override
	public int getCost() {
		int cost = 0;
		for (Matcher m : matchers)
			cost += m.getCost();
		return cost;
	}

	@Override
	public String getExecutionReport() {
		StringBuffer sb = new StringBuffer();
		for (Matcher m : matchers)
			sb.append(m.getExecutionReport()).append("\n");
		return sb.toString();
	}

}