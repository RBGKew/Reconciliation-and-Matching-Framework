package org.kew.shs.dedupl.reporters;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="reporters")
public class DedupReporter extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size", "from_id", "ids_in_cluster"};
	
	public DedupReporter() {
		logger.info("I will be creating an enhanced output file for you with additional fields: " +
				this.getAvailableFieldsAsString());
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
        String namespace = this.getNameSpacePrefix();
		Map<String, String> topCopy = cluster.get(0);
		topCopy.put(namespace + "cluster_size", this.getClusterSize(fromRecord, cluster));
		topCopy.put(namespace + "from_id", this.getFromId(fromRecord, cluster));
		topCopy.put(namespace + "ids_in_cluster", this.getIDsInCluster(fromRecord, cluster, this.getIdDelimiter()));
		this.writer.write(topCopy, this.header);
	}

}
