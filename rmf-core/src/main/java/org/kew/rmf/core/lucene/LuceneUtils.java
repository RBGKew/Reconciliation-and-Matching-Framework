package org.kew.rmf.core.lucene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.kew.rmf.core.configuration.Configuration;
import org.kew.rmf.core.configuration.Property;
import org.kew.rmf.matchers.MatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class to
 * → map from Maps to Lucene Documents and vice versa
 * → build a query string that lucene understands
 * → check whether two strings match according to the configured
 *     {@link org.kew.rmf.matchers.Matcher}
 */
public class LuceneUtils {

    private static Logger logger = LoggerFactory.getLogger(LuceneUtils.class);

    public static String doc2String(Document doc){
        return doc2String(doc, "");
    }

    public static Map<String,String> doc2Map(Document doc){
        Map<String,String> map = new HashMap<String, String>();
        for (IndexableField f : doc.getFields()){
            map.put(f.name(), f.stringValue());
        }
        return map;
    }

    public static Document map2Doc(Map<String, String> map) {
        Document doc = new Document();
        for (String key:map.keySet()) {
            String value = map.get(key);
            value = (value != null) ? value: "";
            doc.add(new TextField(key, value, Field.Store.YES));
        }
        return doc;
    }

    public static String doc2String(Document doc, String prefix){
        StringBuffer sb = new StringBuffer();
        for (IndexableField f : doc.getFields()){
            sb.append(prefix)
                .append(f.name()).append(" : " ).append(doc.getField(f.name()).stringValue())
                .append("\n");
        }
        return sb.toString();
    }

    public static String doc2Line(Document doc, String fieldSeparator){
        StringBuffer sb = new StringBuffer();
        for (IndexableField f : doc.getFields()){
            if (sb.length() > 0)
                sb.append(fieldSeparator);
            sb.append(doc.getField(f.name()).stringValue());
        }
        return sb.toString();
    }

    public static String buildQuery(List<Property> properties, Document doc, boolean dedupl){
        Map<String,String> map = doc2Map(doc);
        return buildQuery(properties, map, dedupl);
    }

    public static String buildQuery(List<Property> properties, Map<String,String> map, boolean dedupl){
        StringBuffer sb = new StringBuffer();
        if (dedupl){
            // Be sure not to return self:
            sb.append("NOT " + Configuration.ID_FIELD_NAME + ":" + map.get(Configuration.ID_FIELD_NAME));
        }
        for (Property p : properties){
            if (p.isUseInSelect() || p.isUseInNegativeSelect()) {
                String authorityName = p.getAuthorityColumnName() + Configuration.TRANSFORMED_SUFFIX;
                String value = map.get(p.getQueryColumnName() + Configuration.TRANSFORMED_SUFFIX);
                // super-csv treats blank as null, we don't for now
                value = (value != null) ? value: "";
                String quotedValue = "\"" + value + "\"";
                if (p.isUseInSelect()){
                    if (StringUtils.isNotBlank(value)){
                        if(p.getMatcher().isExact()){
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(authorityName + ":" + quotedValue);
                        }
                        if (p.isIndexLength()){
                            int low = Math.max(0, value.length()-2);
                            int high = value.length()+2;
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(authorityName).append(Configuration.LENGTH_SUFFIX);
                            sb.append(String.format(":[%02d TO %02d]", low, high));
                        }
                        if (p.isIndexInitial()){
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(authorityName).append(Configuration.INITIAL_SUFFIX).append(':').append(quotedValue.substring(0, 2)).append('"');
                        }
                        if (p.isUseWildcard()){
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(authorityName).append(":").append(quotedValue.subSequence(0, quotedValue.length()-1)).append("~0.5\"");
                        }
                    }
                }
                else { // isUseInNegativeSelect
                    if (StringUtils.isNotBlank(value)){
                        if (sb.length() > 0) {
                            sb.append(" AND");
                        }
                        sb.append(" NOT " + authorityName + ":" + quotedValue);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static boolean recordsMatch(Document from, Document to, List<Property> properties) throws Exception{
        Map<String,String> map = doc2Map(from);
        return recordsMatch(map, to, properties);
    }

	public static boolean recordsMatch(Map<String,String> queryRecord, Document authorityRecord, List<Property> properties) throws MatchException {
		if (logger.isTraceEnabled()) {
			logger.trace("Comparing records: Q:{} A:{}", queryRecord.get(Configuration.ID_FIELD_NAME), authorityRecord.get(Configuration.ID_FIELD_NAME));
		}

		boolean recordMatch = false;

		for (Property p : properties) {
			String queryName = p.getQueryColumnName() + Configuration.TRANSFORMED_SUFFIX;
			String authorityName = p.getAuthorityColumnName() + Configuration.TRANSFORMED_SUFFIX;

			String query = queryRecord.get(queryName);
			query = (query != null) ? query : "";

			String authority = authorityRecord.get(authorityName);
			authority= (authority != null) ? authority : "";

			boolean fieldMatch = false;

			if (p.isBlanksMatch()){
				if (StringUtils.isBlank(query)) {
					fieldMatch = true;
					logger.trace("Q:'' ? A:'{}' → true (blank query)", authority);
				}
				else if (StringUtils.isBlank(authority)) {
					fieldMatch = true;
					logger.trace("Q:'{}' ? A:'' → true (blank authority)", query);
				}
			}

			if (!fieldMatch) {
				fieldMatch = p.getMatcher().matches(query, authority);
				logger.trace("Q:'{}' ? A:'{}' → {}", query, authority, fieldMatch);
			}

			recordMatch = fieldMatch;
			if (!recordMatch) {
				logger.trace("Failed on {}", queryName);
				break;
			}
		}
		return recordMatch;
	}
}
