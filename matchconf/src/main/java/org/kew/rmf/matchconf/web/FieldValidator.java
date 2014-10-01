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
package org.kew.rmf.matchconf.web;

import org.apache.commons.lang3.StringUtils;

public class FieldValidator {

    /**
     * We use many strings as part of the REST-ish url structure;
     * These only want to contain ASCII letters and/or '-', '_'
     * @param s
     * @return
     */
    public static boolean validSlug(String s) {
        return !StringUtils.isBlank(s) && s.matches("[\\w-_]*");
    }

}
