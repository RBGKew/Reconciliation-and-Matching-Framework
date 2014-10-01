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

import org.kew.rmf.transformers.StripNonAlphanumericCharactersTransformer;
import org.kew.rmf.transformers.authors.StripExAuthorTransformer;
import org.kew.rmf.transformers.authors.StripInAuthorTransformer;

/**
 * This matcher tests for common tokens in two authorship strings
 * @author nn00kg
 */
public class AuthorCommonTokensMatcher extends CommonTokensMatcher{

    public static int COST = 1;

    final private StripInAuthorTransformer inCleaner = new StripInAuthorTransformer();
    final private StripExAuthorTransformer exCleaner = new StripExAuthorTransformer();
    final private StripNonAlphanumericCharactersTransformer stripNonDs = new StripNonAlphanumericCharactersTransformer();

    @Override
    public int getCost() {
        return COST;
    }

    @Override
    public boolean matches(String s1, String s2) {
        boolean matches = false;
        if (s1 == null && s2 == null)
            matches = true;
        else{
            matches = super.matches(clean(s1),clean(s2));
        }
        return matches;
    }

    private String clean(String s) {
        return this.inCleaner.transform(this.exCleaner.transform(this.stripNonDs.transform(s)));
    }

    @Override
    public boolean isExact() {
        return false;
    }

    @Override
    public String getExecutionReport() {
        return null;
    }
}
