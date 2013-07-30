package org.kew.shs.dedupl.reporters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class DedupReporterMultiline extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size", "best_record_id", "rank"};

	public DedupReporterMultiline() {
		log.info("I will be creating a file containing a row for each record in a cluster; additional fields: " +
				this.getAvailableFieldsAsString());
	}

	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), DedupReporterMultiline.AVAILABLE_FIELDS);
	}

	@Override
	public void reportResults(Map<String, String> fromRecord, List<Map<String, String>> cluster) throws IOException {
		Map<String, String> topCopy = cluster.get(0);
		topCopy.put("cluster_size", this.getClusterSize(fromRecord, cluster));
		topCopy.put("best_record_id", this.getBestRecordId(cluster));
		topCopy.put("rank", this.getRank(fromRecord, cluster));
		this.writer.write(topCopy, this.header);
	}

}
