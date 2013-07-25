package org.kew.shs.dedupl.lucene;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.shs.dedupl.DataHandler;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.MatchConfiguration;
import org.kew.shs.dedupl.configuration.Property;
import org.kew.shs.dedupl.reporters.LuceneReporter;
import org.kew.shs.dedupl.transformers.Transformer;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;


public class LuceneMatcher extends LuceneHandler<MatchConfiguration> implements DataHandler<MatchConfiguration> {

    protected MatchConfiguration matchConfig;

    public void loadData() throws Exception{ // from DataMatcher
        if (!getConfig().isReuseIndex()){
            this.dataLoader.setConfig(this.getConfig());
            this.dataLoader.load(this.getConfig().getStoreFile());
        }
        else
            log.info("Reusing existing index");
    }

    public void run() throws Exception {

        this.loadData(); // writes the index according to the configuration

        Set<String> alreadyProcessed = new HashSet<String>();

        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.getConfig().getSourceFileDelimiter().charAt(0), "\n").build();
        int i = 0;
        try (MatchConfiguration config = this.getConfig();
             IndexWriter indexWriter = this.indexWriter;
             CsvMapReader mr = new CsvMapReader(new FileReader(this.getConfig().getSourceFile()), customCsvPref)) {

            log.debug(new java.util.Date(System.currentTimeMillis()));

            // Sort properties in order of cost:
            Collections.sort(config.getProperties(),  new Comparator<Property>() {
                public int compare(final Property p1,final Property p2) {
                    return Integer.valueOf(p1.getMatcher().getCost()).compareTo(Integer.valueOf(
                            p2.getMatcher().getCost()));
                }
            });
            // loop over the sourceFile
            int numMatches = 0;
            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertyLookupColumnNames()) {
                if (!headerList.contains(name)) throw new Exception(String.format("Header doesn't contain field name < %s > as defined in config.", name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new Exception(String.format("Id field name not found in header, should be %s!", idFieldName));
            Map<String, String> record;
            while((record = mr.read(header)) != null) {

                if (i++ % config.getAssessReportFrequency() == 0)
                    log.info("Assessed " + i + " records, found " + numMatches + " matches");

                // We now have this record as a hashmap, transformed etc as the data stored in Lucene has been
                String fromId = record.get(Configuration.ID_FIELD_NAME);

                // Keep a record of the records already processed, so as not to return
                // matches like id1:id2 *and* id2:id1
                alreadyProcessed.add(fromId);

                // Use the properties to select a set of documents which may contain matches
                String querystr = LuceneUtils.buildQuery(config.getProperties(), record, false);

                TopDocs td = queryLucene(querystr, this.getIndexSearcher());
                log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);

                DocList matches = null;

                for (ScoreDoc sd : td.scoreDocs){
                    Document toDoc = getFromLucene(sd.doc);
                    log.debug(LuceneUtils.doc2String(toDoc));

                    if (LuceneUtils.recordsMatch(record, toDoc, config.getProperties())){
                        numMatches++;
                        if (matches == null) {
                            // TODO: might have to convert the sourceRecord to a Document instead, not sure..
                            matches = new DocList(toDoc, config.getScoreField());
                        } else matches.add(toDoc);
                    }
                }
                matches.sort();
                // call each reporter that has a say; all they get is a complete list of duplicates for this record.
                for (LuceneReporter reporter : config.getReporters()) {
                    // TODO: make idFieldName configurable, but not on reporter level
                    reporter.setIdFieldName(Configuration.ID_FIELD_NAME);
                    reporter.report(matches);
                }
            }
        }
    }

    public Map<String,String> line2Map(String line, int numColumns){
        Map<String,String> map = new HashMap<String,String>();
        String[] elem = line.split(this.getConfig().getSourceFileDelimiter(), numColumns+1);
        map.put(Configuration.ID_FIELD_NAME, elem[0]);
        for (Property p : config.getProperties()){
            // TODO: fix this using super-csv
            String value = "hallo";//elem[p.getColumnIndex()];
            // Save original value if required
            if (p.isIndexOriginal())
                map.put(p.getLookupColumnName() + Configuration.ORIGINAL_SUFFIX,value);
                    // Transform the value if necessary
            for (Transformer t:p.getSourceTransformers()) {
                value = t.transform(value);
            }
            // Save into map
            map.put(p.getLookupColumnName(),value);
            // Save length if required
            if (p.isIndexLength()){
                int length = 0;
                if (value != null)
                    length = value.length();
                map.put(p.getLookupColumnName() + Configuration.LENGTH_SUFFIX,String.format("%02d", length));
            }
            if (p.isIndexInitial()){
                String init = "";
                if (StringUtils.isNotBlank(value)) init = value.substring(0,1);
                map.put(p.getLookupColumnName() + Configuration.INITIAL_SUFFIX, init);
            }
        }
        return map;
    }

}
