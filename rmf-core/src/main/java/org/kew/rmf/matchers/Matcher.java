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
 * This interface defines the behaviour expected of Matchers.
 * Some matches are expensive to calculate, so matchers have a cost:
 * zero - low, higher int values = higher cost.
 * When multiple matchers are defined, it is expected that they will be 
 * evaluated in order of cost (lowest first).
 * 
 * @author nn00kg
 */
public interface Matcher {

	public boolean matches(String s1, String s2) throws MatchException;
	public boolean isExact();
	public int getCost();
	public String getExecutionReport();
}
