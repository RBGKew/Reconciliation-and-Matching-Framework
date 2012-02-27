package org.kew.shs.dedupl.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.kew.shs.dedupl.Configuration;

public class LuceneUtils {

	public static String doc2String(Document doc){
		return doc2String(doc, "");
	}

	public static String doc2String(Document doc, String prefix){
		StringBuffer sb = new StringBuffer();
		for (Fieldable f : doc.getFields()){
			sb.append(prefix)
				.append(f.name()).append(" : " ).append(doc.getFieldable(f.name()).stringValue())
				.append("\n");
		}
		return sb.toString();
	}
	
	public static String docs2ComparisonString(Document doc1, Document doc2){
		return docs2ComparisonString(doc1, doc2, "#");
	}

	/**
	 * Return a string containing the field names and values for those fields that 
	 * differ in value between the two records supplied.
	 * @param doc1
	 * @param doc2
	 * @param prefix
	 * @return
	 */
	public static String docs2ComparisonString(Document doc1, Document doc2, String prefix){
		StringBuffer sb = new StringBuffer();
		for (Fieldable f : doc1.getFields()){
			if (!f.name().equals(Configuration.ID_FIELD_NAME)){
				String v1 = doc1.getFieldable(f.name()).stringValue();
				String v2 = doc2.getFieldable(f.name()).stringValue();
				if (!v1.equals(v2)){
					sb.append(prefix).append(f.name()).append("\n");
					sb.append(prefix).append(v1).append("\n");
					sb.append(prefix).append(v2).append("\n");
				}
			}
		}
		return sb.toString();
	}

}
