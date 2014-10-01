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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class DedupReporter extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size", "from_id", "ids_in_cluster"};

	public DedupReporter() {
		logger.info("I will be creating an enhanced output file for you with additional fields: " +
				this.getAvailableFieldsAsString());
	}

	@Override
	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), DedupReporter.AVAILABLE_FIELDS);
	}

	/*
	 * Returns one line for one identified cluster containing the stored fields for
	 * the 'best' record + additional info
	 *
	 */
	@Override
	public void reportResults(Map<String, String> fromRecord, List<Map<String, String>> cluster) throws Exception {
        String namespace = this.getNameSpacePrefix();
		Map<String, String> topCopy = cluster.get(0);
		topCopy.put(namespace + "cluster_size", this.getClusterSize(fromRecord, cluster));
		topCopy.put(namespace + "from_id", this.getFromId(fromRecord, cluster));
		topCopy.put(namespace + "ids_in_cluster", this.getIDsInCluster(fromRecord, cluster, this.getIdDelimiter()));
		this.writer.write(topCopy, this.header);
	}

}
