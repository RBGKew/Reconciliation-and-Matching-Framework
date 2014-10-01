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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This matcher calculates how many tokens are shared between two strings, the tokens
 * If the calculated ratio is above the `minRatio` it returns true.
 * @author nn00kg
 */
public class CommonTokensMatcher extends TokeniserMatcher {

    public static int COST = 5;
    protected double minRatio = 0.5;
    private boolean uniqueCommonTokens = false;

    private static Logger logger = LoggerFactory.getLogger(CommonTokensMatcher.class);

    @Override
    public int getCost() {
        return COST;
    }

    @Override
    public boolean matches(String s1, String s2) {
        logger.trace("s1: {}", s1);
        logger.trace("s2: {}", s2);
        if (s1 == null && s2 == null) return true;
        String[] a1 = convToArray(s1);
        logger.trace("{}", (Object[]) a1);
        String[] a2 = convToArray(s2);
        logger.trace("{}", (Object[]) a2);
        return calculateTokensInCommon(a1,a2);
    }

    public Boolean calculateTokensInCommon(String[] s1, String[] s2){
        Collection<String> common = new ArrayList<String>(Arrays.asList(s1));
        common.retainAll(Arrays.asList(s2));
        if (this.isUniqueCommonTokens()) common = new HashSet<String>(common);
        int numCommon = common.size();
        logger.trace("Number of tokens in common: {}", numCommon);

        double ratio = (Double.valueOf(numCommon) / Double.valueOf((numCommon + (s1.length - numCommon) + (s2.length - numCommon))));
        logger.trace("ratio = {}", ratio);

        return ratio >= this.minRatio;
    }

    public double getMinRatio() {
        return this.minRatio;
    }

    public void setMinRatio(double minRatio) {
        this.minRatio = minRatio;
    }

    @Override
    public boolean isExact() {
        return false;
    }

    @Override
    public String getExecutionReport() {
        return null;
    }

    public boolean isUniqueCommonTokens() {
        return uniqueCommonTokens;
    }

    public void setUniqueCommonTokens(boolean uniqueCommonTokens) {
        this.uniqueCommonTokens = uniqueCommonTokens;
    }
}
