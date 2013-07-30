package org.kew.shs.dedupl.reporters;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class DedupReporter extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size", "from_id", "ids_in_cluster"};
	
	public DedupReporter() {
		log.info("I will be creating an enhanced output file for you with additional fields: " +
				this.getAvailableFields());
	}

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
		Map<String, String> topCopy = cluster.get(0);
		topCopy.put("cluster_size", this.getClusterSize(fromRecord, cluster));
		topCopy.put("from_id", this.getFromId(fromRecord, cluster));
		topCopy.put("ids_in_cluster", this.getIDsInCluster(fromRecord, cluster, this.getIdDelimiter()));
		this.writer.write(topCopy, this.header);
	}

}
