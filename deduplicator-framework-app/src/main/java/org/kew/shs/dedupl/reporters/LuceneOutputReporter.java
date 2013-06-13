package org.kew.shs.dedupl.reporters;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.document.Document;
import org.kew.shs.dedupl.lucene.DocList;
import org.kew.shs.dedupl.lucene.LuceneUtils;

public class LuceneOutputReporter extends LuceneReporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"ids_in_cluster"};

	public LuceneOutputReporter(File file, String delimiter, String scoreFieldName, String idFieldName)
			throws IOException {
		super(file, delimiter, scoreFieldName, idFieldName);
		log.info("I will be creating an enhanced output file for you with additional fields: " +
				this.getAvailableFieldsAsString());
	}

	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), LuceneOutputReporter.AVAILABLE_FIELDS);
	}

	/*
	 * Returns one line for one identified cluster containing the stored fields for
	 * the 'best' record + additional info
	 *
	 */
	@Override
	public void report (DocList docs) throws IOException {
		if (this.isStart && this.wantHeader) {
			this.writeHeader(docs);
			this.setStart(false);
		}
		// 1. rank the cluster-duplicates according to their score
		docs.sort();
		// 2. get the bestDoc (NOTE: might also be the one used for the lookup!)
		Document bestDoc = docs.get(0);
		// 3. populate the row to write with all fields for this best record from what is stored in lucene
		String row = LuceneUtils.doc2Line(bestDoc, this.delimiter);
		// 4. add some additional fields
		row += this.getClusterSize(docs);
		row += this.getFromId(docs);
		row += this.getIDsInCluster(docs);
		row += "\n";
		this.bw.write(row);
	}

}
