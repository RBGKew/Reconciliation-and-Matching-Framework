package org.kew.shs.dedupl.reporters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.kew.shs.dedupl.lucene.LuceneUtils;

public class LuceneOutputReporter extends LuceneReporter {

	protected static String[] ADDED_FIELDNAMES = new String[] {"ids_in_cluster"};
	
	public LuceneOutputReporter(File file, String delimiter, String scoreFieldName, String idFieldName)
			throws IOException {
		super(file, delimiter, scoreFieldName, idFieldName);
		log.info("Hello, I will be creating an enhanced output file for you.");
	}

	public void report (List<Document> docs) throws IOException {
		Document bestDoc = this.selectBestDocument(docs);
		String row = LuceneUtils.doc2Line(bestDoc,this.delimiter);
		row += this.getIDsInCluster(docs);
		row += this.getClusterSize(docs);
		row += this.getSourceID(docs);
		row += "\n";
		this.bw.write(row);
	}	
	
	private String getIDsInCluster(List<Document> docs) {
		String ids = this.delimiter;
		for (Document doc : docs) {
			if (docs.indexOf(doc) > 0)
				ids += " | ";
			ids += (doc.get(this.idFieldName));
		}
		return ids;
	} 
	
	private String getClusterSize(List<Document> docs) {
		return this.delimiter + docs.size();
	}
	
	private String getSourceID(List<Document> docs) {
		return this.delimiter + docs.get(0).get(this.idFieldName);
	}
}
