package org.kew.shs.dedupl.reporters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

public abstract class LuceneReporter extends Reporter {

	private int num_tied_scores = 0;
	
	public LuceneReporter(File file, String delimiter, String scoreFieldName, String idFieldName) throws IOException {
		super(file, delimiter, scoreFieldName, idFieldName);
	}

	// this method should call the report (String) method at the end, not sure how to tell java
	public abstract void report (List<Document> docs) throws IOException;

	protected Document selectBestDocument(List<Document> docs){
		Document doc = null;
		if (docs != null){
			doc = docs.get(0);
			if (docs.size() > 1){
				if (doc.get(scoreFieldName).equals(docs.get(1).get(scoreFieldName))){
					num_tied_scores++;
				}
			}
		}
		return doc; 
	}


}
