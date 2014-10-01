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

public class DedupReporterMultiline extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size", "best_record_id", "rank"};

	public DedupReporterMultiline() {
		logger.info("I will be creating a file containing a row for each record in a cluster; additional fields: " +
				this.getAvailableFieldsAsString());
	}

	@Override
	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), DedupReporterMultiline.AVAILABLE_FIELDS);
	}

	@Override
	public void reportResults(Map<String, String> fromRecord, List<Map<String, String>> cluster) throws IOException {
        String namespace = this.getNameSpacePrefix();
		// TODO: make sure the fromRecord is in the cluster (should be!)
        for (Map<String, String> record:cluster) {
			record.put(namespace + "cluster_size", this.getClusterSize(fromRecord, cluster));
			record.put(namespace + "best_record_id", this.getBestRecordId(cluster));
			record.put(namespace + "rank", this.getRank(record, cluster));
			this.writer.write(record, this.header);
        }
	}

}
