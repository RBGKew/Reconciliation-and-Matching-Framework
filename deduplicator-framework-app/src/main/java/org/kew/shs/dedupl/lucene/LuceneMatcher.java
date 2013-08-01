package org.kew.shs.dedupl.lucene;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.kew.shs.dedupl.reporters.Piper;
import org.kew.shs.dedupl.script.JavaScriptEnv;
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

        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference csvPref = new CsvPreference.Builder(
                '"', this.getConfig().getSourceFileDelimiter().charAt(0), "\n").build();
        int i = 0;
        try (MatchConfiguration config = this.getConfig();
             IndexWriter indexWriter = this.indexWriter;
             CsvMapReader mr = new CsvMapReader(new FileReader(this.getConfig().getSourceFile()), csvPref)) {

            // copy some necessary values to reporters and possibly create pipers if recordFilter is set
            config.setupReporting();
            // the recordFilter (excludes records from processing if condition fulfilled) needs a JavaScript environment set up
            JavaScriptEnv jsEnv = null;
            if (!StringUtils.isBlank(config.getRecordFilter())) {
                jsEnv = new JavaScriptEnv();
                log.debug("Record filter activated, javascript rock'n roll!");
            }
            // DEFUNCTED! messed up the order of columns. TODO: possibly implement again differently
            // Sort properties in order of cost:
//            Collections.sort(config.getProperties(),  new Comparator<Property>() {
//                public int compare(final Property p1,final Property p2) {
//                    return Integer.valueOf(p1.getMatcher().getCost()).compareTo(Integer.valueOf(
//                            p2.getMatcher().getCost()));
//                }
//            });

            // loop over the sourceFile
            int numMatches = 0;
            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertySourceColumnNames()) {
                if (!headerList.contains(name)) throw new Exception(String.format("Header doesn't contain field name < %s > as defined in config.", name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new Exception(String.format("Id field name not found in header, should be %s!", idFieldName));
            Map<String, String> record;
            while((record = mr.read(header)) != null) {
                if (!StringUtils.isBlank(config.getRecordFilter()) && jsEnv.evalFilter(config.getRecordFilter(), record)) {
                    for (Piper piper:config.getPipers()) piper.pipe(record);
                }

                // transform fields where required
                for (Property prop:config.getProperties()) {
                    String fName = prop.getSourceColumnName();
                    String fValue = record.get(fName);
                    // first copy the field values to a new '*_orig' field if required
                    if (prop.isAddOriginalSourceValue()) record.put(fName + "_orig", fValue);
                    // then transform the field-values in place
                    if (prop.isAddTransformedSourceValue()) {
                        fValue = fValue == null ? "" : fValue; // super-csv treats blank as null, we don't for now
                        for (Transformer t:prop.getSourceTransformers()) {
                            record.put(fName, t.transform(fValue));
                        }
                    }
                }

                String fromId = record.get(Configuration.ID_FIELD_NAME);

                // Use the properties to select a set of documents which may contain matches
                String querystr = LuceneUtils.buildQuery(config.getProperties(), record, false);

                TopDocs td = queryLucene(querystr, this.getIndexSearcher());
                log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);

                DocList matches = new DocList(record, config.getScoreFieldName());

                for (ScoreDoc sd : td.scoreDocs){
                    Document toDoc = getFromLucene(sd.doc);
                    if (LuceneUtils.recordsMatch(record, toDoc, config.getProperties())){
                        numMatches++;
                        if (matches == null) {
                            // TODO: might have to convert the sourceRecord to a Document instead, not sure..
                            matches = new DocList(toDoc, config.getScoreFieldName());
                        } else matches.add(toDoc);
                    }
                }
                matches.sort();

                if (i++ % config.getAssessReportFrequency() == 0) log.info("Assessed " + i + " records, found " + numMatches + " matches");
                // call each reporter that has a say; all they get is a complete list of duplicates for this record.
                for (LuceneReporter reporter : config.getReporters()) {
                    // TODO: make idFieldName configurable, but not on reporter level
                    reporter.report(matches);
                }

            }
            log.info("Assessed " + i + " records, found " + numMatches + " matches");
        }
    }

}
