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
import org.kew.rmf.utils.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This matcher uses the Levenshtein edit distance algorithm
 * (provided by the Apache StringUtils class).
 * It is computationally expensive, so has a cost of 10.
 * The maximum distance is configurable.
 * @author nn00kg
 */
public class LevenshteinMatcher implements Matcher {

    public static int COST = 10;
    private int maxDistance = 2;

    private int numCalls = 0;
    private int numExecutions = 0;

    private static Logger logger = LoggerFactory.getLogger(LevenshteinMatcher.class);

    private Dictionary dictionary;

    public Dictionary getDictionary() {
        return dictionary;
    }
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public int getCost() {
        return COST;
    }

    @Override
    public boolean matches(String s1, String s2) throws MatchException {
        boolean matches = false;
        numCalls++;
        if (StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2)) return true;
        if (StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2)) {
            matches = s1.equals(s2);
            if (!matches){
                logger.trace("Testing ld({}, {})", s1, s2);
                int shorter = Math.min(s1.length(), s2.length());
                int longer = Math.max(s1.length(), s2.length());
                if ((longer - shorter) > maxDistance)
                    matches = false;
                else {
                    int distance = calculateLevenshtein(s1, s2).intValue();
                    matches = (distance <= maxDistance);
                }
            }
        }
        if (this.getDictionary() != null && matches) {
            matches = doFalsePositiveCheck(s1,s2);
        }
        return matches;
    }

	private boolean doFalsePositiveCheck(String s1, String s2) {
		logger.info("FALSE_POSITIVES_CHECK");
		boolean passed = true;
		Dictionary falsePositives = this.getDictionary();
		if (falsePositives.get(s1) != null && falsePositives.get(s1).equals(s2)) {
			passed = false;
		}
		else if (falsePositives.get(s2) != null && falsePositives.get(s2).equals(s1)) {
			passed = false;
		}
		if (!passed) {
			logger.info("Rejected match (" + s1 + ", " + s2 + ") as false positive");
		}
		return passed;
	}

    public Integer calculateLevenshtein(String s1, String s2){
        numExecutions++;
        return new Integer(StringUtils.getLevenshteinDistance(s1, s2));
    }

    @Override
    public boolean isExact() {
        return false;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public String getExecutionReport(){
        return "Number of calls: "+ numCalls + ", num executions: " + numExecutions;
    }
}
