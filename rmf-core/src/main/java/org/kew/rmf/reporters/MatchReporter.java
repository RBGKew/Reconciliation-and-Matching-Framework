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
package org.kew.rmf.reporters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class MatchReporter extends LuceneReporter {

    protected static String[] AVAILABLE_FIELDS = new String[] {"configLog", "total_matches", "matching_ids"};

    public MatchReporter() {
        logger.info("I will be creating an enhanced output file for you with additional fields: " +
                this.getAvailableFieldsAsString());
    }

    @Override
    protected String[] getAvailableFields () {
        return (String[]) ArrayUtils.addAll(super.getAvailableFields(), MatchReporter.AVAILABLE_FIELDS);
    }

    /**
     * Returns one line for one identified cluster containing the stored fields for
     * the 'best' record + additional info
     */
    @Override
    public void reportResults (Map<String, String> fromRecord, List<Map<String, String>> matches) throws IOException {
        String namespace = this.getNameSpacePrefix();
        fromRecord.put("configLog", this.getConfigLog());
        fromRecord.put(namespace + "total_matches", this.getClusterSize(fromRecord, matches));
        fromRecord.put(namespace + "matching_ids", this.getIDsInCluster(fromRecord, matches, this.getIdDelimiter()));
        // add the authority values of the best match
        if (matches.size() > 0) fromRecord.putAll(Reporter.getNamespacedCopy(matches.get(0), namespace + "authority_"));
        this.writer.write(fromRecord, this.header);
    }
}
