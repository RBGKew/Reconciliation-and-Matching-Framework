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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DarwinCoreArchiveExtensionReporter extends LuceneReporter {

    protected static String[] AVAILABLE_FIELDS = new String[] {"id", "matching_ids"};

    public DarwinCoreArchiveExtensionReporter() {
        logger.info("I will be creating an enhanced output file for you with additional fields: " +
                this.getAvailableFieldsAsString());
    }

    @Override
    protected String[] getAvailableFields () {
        return new String[0];
    }

    @Override
	protected void writeHeader() throws IOException {
        this.header = AVAILABLE_FIELDS;
        this.writer.writeHeader(header);
    }

    /**
     * Returns one line for one identified cluster containing the stored fields for
     * the 'best' record + additional info
     */
    @Override
    public void reportResults (Map<String, String> fromRecord, List<Map<String, String>> matches) throws IOException {
        String namespace = this.getNameSpacePrefix();
//        fromRecord.put("configLog", this.getConfigLog());
//        fromRecord.put(namespace + "total_matches", this.getClusterSize(fromRecord, matches));
        
        this.getIDsInCluster(fromRecord, matches, this.getIdDelimiter());
        
        String highestScoringMatch = getHighestScoringMatch(fromRecord, matches, this.getIdDelimiter());
        fromRecord.put(namespace + "matching_ids", highestScoringMatch);

        // add the authority values of the best match
        //if (matches.size() > 0) fromRecord.putAll(Reporter.getNamespacedCopy(matches.get(0), namespace + "authority_"));
        if (highestScoringMatch != null) this.writer.write(fromRecord, this.header);
    }

	protected String getHighestScoringMatch(Map<String, String> fromRecord, List<Map<String, String>> cluster, String delimiter) {
		double bestScore = -1;
		double s;
		String bestId = null;
		for (Map<String, String> record : cluster) {
			s = Double.parseDouble(record.get("_score"));
			if (s > bestScore) {
				bestId = record.get(this.idFieldName);
				bestScore = s;
			}
		}
		return bestId;
	}
}
