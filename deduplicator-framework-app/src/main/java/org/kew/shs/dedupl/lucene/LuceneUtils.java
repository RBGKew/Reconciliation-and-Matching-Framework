package org.kew.shs.dedupl.lucene;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            doc.add(new Field(key, value, Field.Store.YES, Field.Index.ANALYZED));
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

    public static String buildComparisonString(List<Property> properties, Document doc1, Document doc2){
        return buildComparisonString(properties, doc2Map(doc1), doc2Map(doc2), "#");
    }

    /**
     * Return a string containing the field names and values for those fields that
     * differ in value between the two records supplied.
     * @param doc1
     * @param doc2
     * @param prefix
     * @return
     */
    public static String buildComparisonString(List<Property> properties, Map<String,String> map, Document doc){
        return buildComparisonString(properties, map, doc2Map(doc), "#");
    }

    public static String buildComparisonString(List<Property> properties, Map<String,String> map, Document doc, String prefix){
        return buildComparisonString(properties, map, doc2Map(doc), prefix);
    }

    public static String buildComparisonString(List<Property> properties, Map<String,String> map1, Map<String,String> map2, String prefix){
        StringBuffer sb = new StringBuffer();
        for (Property p : properties){
            String key = p.getLookupColumnName();
            if (!key.equals(Configuration.ID_FIELD_NAME)){
                String v1 = map1.get(key);
                String v2 = map2.get(key);
                if (!v1.equals(v2)){
                    sb.append(prefix).append(key).append("\n");
                    sb.append(prefix).append(v1).append("\n");
                    sb.append(prefix).append(v2).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Return a string containing all the matched field names and comparison metrics
     * String is written to a file which can be loaded into a db table for further analysis
     * @param doc1
     * @param doc2
     * @param prefix
     * @return
     */
    public static String buildFullComparisonString(Map<String,String> map, Document doc){
        return buildFullComparisonString(map, doc2Map(doc), "#");
    }

    public static String buildFullComparisonString(Map<String,String> map, Document doc, String prefix){
        return buildFullComparisonString(map, doc2Map(doc), prefix);
    }

    public static String buildFullComparisonString(Map<String,String> map1, Map<String,String> map2, String prefix){
        StringBuffer sb = new StringBuffer();
        for (String key : map1.keySet()){
               String v1 = map1.get(key);
               String v2 = map2.get(key);
                //if (!v1.equals(v2)){
                    //sb.append(key).append(prefix);
                    sb.append(v1).append(prefix);
                    sb.append(v2).append(prefix);

                //}

        }
        sb.append("\n");
        return sb.toString();
    }

    public static String buildNoMatchDelimitedString(Map<String,String> map){
        return buildNoMatchDelimitedString(map, "#");
    }

    public static String buildNoMatchDelimitedString(Map<String,String> map, String prefix){
        StringBuffer sb = new StringBuffer();
        for (String key : map.keySet()){
               String v1 = map.get(key);
               sb.append(v1).append(prefix);
               sb.append("").append(prefix);
        }
               sb.append("\n");
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
                String lookupName = p.getLookupColumnName() + Configuration.TRANSFORMED_SUFFIX;
                String value = map.get(p.getSourceColumnName() + Configuration.TRANSFORMED_SUFFIX);
                // super-csv treats blank as null, we don't for now
                value = (value != null) ? value: "";
                String quotedValue = "\"" + value + "\"";
                if (p.isUseInSelect()){
                    if (StringUtils.isNotBlank(value)){
                        if(p.getMatcher().isExact()){
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(lookupName + ":" + quotedValue);
                        }
                        if (p.isIndexLength()){
                            int low = Math.max(0, value.length()-2);
                            int high = value.length()+2;
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(" ").append(lookupName + Configuration.LENGTH_SUFFIX + ":[").append(String.format("%02d", low)).append(" TO ").append(String.format("%02d", high)).append("]");
                        }
                        if (p.isIndexInitial()){
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(lookupName + Configuration.INITIAL_SUFFIX).append(":").append(quotedValue.substring(0, 2) + "\"");
                        }
                        if (p.isUseWildcard()){
                            if (sb.length() > 0) sb.append(" AND ");
                            sb.append(lookupName).append(":").append(quotedValue.subSequence(0, quotedValue.length()-1)).append("~0.5\"");
                        }
                    }
                }
                else {
                    if (StringUtils.isNotBlank(value)){
                        if (sb.length() > 0) sb.append(" AND ");
                            sb.append(" NOT " + lookupName + ":" + quotedValue);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static boolean recordsMatch(Document from, Document to, List<Property> properties){
        Map<String,String> map = doc2Map(from);
        return recordsMatch(map, to, properties);
    }

    public static boolean recordsMatch(Map<String,String> from, Document to, List<Property> properties){
        boolean recordMatch = false;
        logger.debug("Comparing records: " + from.get(Configuration.ID_FIELD_NAME) + " " + to.get(Configuration.ID_FIELD_NAME));
        for (Property p : properties){
            String sourceName = p.getSourceColumnName() + Configuration.TRANSFORMED_SUFFIX;
            String lookupName = p.getLookupColumnName() + Configuration.TRANSFORMED_SUFFIX;
            String s1 = from.get(sourceName);
            s1 = (s1 != null) ? s1: "";
            String s2 = to.get(lookupName);
            s2= (s2 != null) ? s2: "";
            boolean fieldMatch = false;
            if (p.isBlanksMatch()){
                fieldMatch = (StringUtils.isBlank(s1) || StringUtils.isBlank(s2));
                if (fieldMatch){
                    logger.debug(sourceName);
                }
            }
            if (!fieldMatch){
                String[] s = new String[2];
                s[0] = s1;
                s[1] = s2;
                Arrays.sort(s);
                fieldMatch = p.getMatcher().matches(s[0], s[1]);
                logger.debug(s[0] + " : " + s[1] + " : " + fieldMatch);
            }
            recordMatch = fieldMatch;
            if (!recordMatch) {
                logger.debug("failed on " + sourceName);
                break;
            }
        }
        return recordMatch;
    }

}
