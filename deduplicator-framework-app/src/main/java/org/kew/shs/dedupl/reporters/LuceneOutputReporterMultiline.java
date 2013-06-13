package org.kew.shs.dedupl.reporters;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.kew.shs.dedupl.lucene.DocList;
import org.kew.shs.dedupl.lucene.LuceneUtils;

public class LuceneOutputReporterMultiline extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"best_record_id", "rank"};

	public LuceneOutputReporterMultiline(File file, String delimiter,
			String scoreFieldName, String idFieldName) throws IOException {
		super(file, delimiter, scoreFieldName, idFieldName);
		log.info("I will be creating a file containing a row for each record in a cluster; additional fields: " +
				this.getAvailableFieldsAsString());
	}

	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), LuceneOutputReporterMultiline.AVAILABLE_FIELDS);
	}

	@Override
	public void report(DocList docs) throws IOException {
		if (this.isStart && this.wantHeader) {
			this.writeHeader(docs);
			this.setStart(false);
		}
		String bestDocId = this.getBestDocId(docs);
		String clusterSize = this.getClusterSize(docs);
		String fromId = this.getFromId(docs);
		String[] ranks = this.getRanks(docs);

		String row = "";
		for (int i=0; i<docs.size(); i++) {
			row = LuceneUtils.doc2Line(docs.get(i), this.delimiter);
			row = row + clusterSize + fromId + bestDocId + ranks[i] + "\n";
			this.bw.write(row);
		}
	}

}
