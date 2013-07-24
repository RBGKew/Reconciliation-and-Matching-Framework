package org.kew.shs.dedupl.reporters;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.kew.shs.dedupl.lucene.DocList;

public abstract class LuceneReporter extends Reporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"from_id"};
	protected boolean isStart = true;

	protected String[] getAvailableFields () {
		return (String[]) ArrayUtils.addAll(super.getAvailableFields(), LuceneReporter.AVAILABLE_FIELDS);
	}

	public LuceneReporter() {
		super();
	}

	protected void writeHeader(DocList docs) throws IOException {
		String [] fieldNames = docs.getFieldNames();
		String standardFields = "";
		String name;
		for (int i=0; i<fieldNames.length; i++) {
			name = fieldNames[i];
			if (i != 0)
				name = this.delimiter + name;
			standardFields += (name);
		}
		this.writeHeader(standardFields);

	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	// this method should call the report (String) method at the end, not sure how to tell java
	public abstract void report (DocList docs) throws IOException;

	protected String getIDsInCluster(DocList docs) {
		return this.delimiter + docs.get_attributes(this.idFieldName, " | ");
	}

	protected String getClusterSize(DocList docs) {
		return this.delimiter + docs.size();
	}

	protected String getFromId(DocList docs) {
		return this.delimiter + docs.getFromDoc().get(this.idFieldName);
	}

	// TODO: For now only ranked according to scoreField values, no match-specific ranking!
	protected String[] getRanks(DocList docs) {
		docs.sort();
		String[] ranks = new String[docs.size()];
		for (int i=0; i<ranks.length; i++) {
			ranks[i] = this.delimiter + (i + 1);
		}
		return ranks;
	}

	protected String getBestDocId(DocList docs) {
		docs.sort();
		return this.delimiter + docs.get(0).get(this.idFieldName);
	}
}
