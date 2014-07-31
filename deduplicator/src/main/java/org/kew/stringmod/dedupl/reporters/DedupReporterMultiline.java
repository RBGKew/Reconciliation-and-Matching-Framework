package org.kew.stringmod.dedupl.reporters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kew.stringmod.utils.LibraryRegister;

@LibraryRegister(category="reporters")
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
