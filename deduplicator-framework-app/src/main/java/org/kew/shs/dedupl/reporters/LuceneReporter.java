package org.kew.shs.dedupl.reporters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.lucene.DocList;

public abstract class LuceneReporter extends Reporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {};

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
