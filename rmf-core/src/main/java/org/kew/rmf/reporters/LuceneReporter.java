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
import org.kew.rmf.core.lucene.DocList;

public abstract class LuceneReporter extends Reporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {};

	@Override
	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), LuceneReporter.AVAILABLE_FIELDS);
	}

	protected void writeHeader(DocList docs) throws IOException {
		this.writeHeader();
	}

	public void report (DocList docs) throws Exception {
		docs.sort();
		this.report(docs.getFromDocAsMap(), docs.storeToMapList());
	}

	protected String getIDsInCluster(Map<String, String> fromRecord, List<Map<String, String>> cluster, String delimiter) {
		List<String> ids = new ArrayList<>();
		for (Map<String, String> record:cluster) {
			ids.add(record.get(this.idFieldName));
		}
		return StringUtils.join(ids, " " + this.idDelimiter + " ");
	}

	protected String getFromId(Map<String, String> fromRecord, List<Map<String, String>> cluster) {
		return fromRecord.get(this.idFieldName);
	}

	// TODO: For now only ranked according to scoreField values, no match-specific ranking!
	protected String getRank(Map<String, String> fromRecord, List<Map<String, String>> cluster) {
		int i = 1;
		for (Map<String, String> rec:cluster) {
			if (fromRecord.get(this.getIdFieldName()).equals(rec.get(this.getIdFieldName()))) {
				break;
			}
			i++;
		}
		return Integer.toString(i);
	}
	
	protected String getConfigLog() {
		return this.configName;
	}

	protected String getBestRecordId(List<Map<String, String>> cluster) {
		return cluster.get(0).get(this.idFieldName);
	}
}
