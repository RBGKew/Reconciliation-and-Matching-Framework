package org.kew.shs.dedupl.lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.shs.dedupl.DataMatcher;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.MatchConfiguration;
import org.kew.shs.dedupl.configuration.Property;
import org.kew.shs.dedupl.transformers.Transformer;

/**
 * This is a Lucene implementation of the Deduplicator interface
 * @author nn00kg
 *
 */
public class LuceneMatcher extends LuceneHandler implements DataMatcher {

    protected MatchConfiguration matchConfig;

    /**
     * Type-casting config to matchConfig here, so that all the time a match
     * config is needed it can be acquired in a cached(ish) way - not sure whether
     * that's the way to do it..
     */
    public MatchConfiguration getMatchConfig() {
        if (this.matchConfig == null) {
            this.matchConfig = (MatchConfiguration) this.getConfig();
        }
        return this.matchConfig;
    }

    public void loadData() throws Exception{ // from DataMatcher
        if (!getMatchConfig().isReuseIndex()){
            this.dataLoader.setConfig(this.getMatchConfig());
            this.dataLoader.load(this.getMatchConfig().getStoreFile());
        }
        else
            log.info("Reusing existing index");
    }

    public void run() throws Exception{ // from DataMatcher

        loadData();

        MatchConfiguration config = this.getMatchConfig();
        // Read something
        Set<String> alreadyProcessed = new HashSet<String>();

        int i = 0;
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(config.getSourceFile()))) {

            log.debug(new java.util.Date(System.currentTimeMillis()));

            indexReader = IndexReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            BufferedWriter bw = new BufferedWriter(new FileWriter(config.getOutputFile()));

            BufferedWriter bw_report = null;
            if (config.isWriteComparisonReport())
                bw_report = new BufferedWriter(new FileWriter(config.getReportFile()));


            BufferedWriter bw_delimitedReport = null;
            if (config.isWriteDelimitedReport())
                bw_delimitedReport = new BufferedWriter(new FileWriter(config.getDelimitedFile()));

            if (getMatchConfig().isOutputAllMatches())
                log.debug("Configured to output all matches");
            else
                log.debug("Configured to only output top match");

            int numMatches = 0;
            // TODO: fix this using super-csv
            int numColumns = 0;//LuceneDataLoader.calculateNumberColumns(config.getProperties());
            int anyMatches = 0;

            while ((line = br.readLine()) != null){

                if (i++ % config.getAssessReportFrequency() == 0)
                    log.info("Assessed " + i + " records, found " + numMatches + " matches");

                Map<String,String> map = line2Map(line, numColumns);

                // We now have this record as a hashmap, transformed etc as the data stored in Lucene has been
                String fromId = map.get(Configuration.ID_FIELD_NAME);

                // Keep a record of the records already processed, so as not to return
                // matches like id1:id2 *and* id2:id1
                alreadyProcessed.add(fromId);

                // Use the properties to select a set of documents which may contain matches
                String querystr = LuceneUtils.buildQuery(config.getProperties(), map, false);

                TopDocs td = queryLucene(querystr, indexSearcher);
                log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);

                anyMatches = 0;

                SortedMap<Object,String> matches = null;
                if (getMatchConfig().isOutputAllMatches()){
                    log.debug("Output all matches");
                    matches = new TreeMap<Object, String>();
                }
                else{
                    log.debug("Only output top match");
                    matches = new TreeMap<Object, String>();
                }
                for (ScoreDoc sd : td.scoreDocs){
                    Document toDoc = getFromLucene(sd.doc);
                    log.debug(LuceneUtils.doc2String(toDoc));

                    String toId = toDoc.get(Configuration.ID_FIELD_NAME);

                    if (LuceneUtils.recordsMatch(map, toDoc, config.getProperties())){
                        numMatches++;
                        anyMatches++;
                        if (getMatchConfig().isOutputAllMatches()){
                            matches.put(toId, toId);
                        }
                        else{
                            String score = toDoc.get(getMatchConfig().getScoreField());
                            log.debug("Match score: " + Integer.valueOf(score));
                            matches.put(Integer.valueOf(score), toId);
                        }
                        if (config.isWriteComparisonReport()){
                            bw_report.write(fromId + config.getOutputFileDelimiter() + toId + "\n");
                            bw_report.write(LuceneUtils.buildComparisonString(config.getProperties(), map, toDoc));
                        }
                        if (config.isWriteDelimitedReport()){
                            bw_delimitedReport.write(LuceneUtils.buildFullComparisonString(map, toDoc));
                        }
                    }
                }
                if (getMatchConfig().isOutputAllMatches()){
                    StringBuffer sb = new StringBuffer();
                    for (String id : matches.values()){
                        if (sb.length() > 0)
                            sb.append(",");
                        sb.append(id);
                    }
                    bw.write(fromId + config.getOutputFileDelimiter() + sb.toString() + "\n");
                }
                else{
                    // only output the highest scoring match
                    String bestMatchId = null;
                    if (!matches.isEmpty()){
                        log.debug("Number of matches: " + matches.size() + ", Last key: " + matches.lastKey());
                        bestMatchId = matches.get(matches.lastKey());
                        bw.write(fromId + config.getOutputFileDelimiter() + bestMatchId + "\n");
                    }
                    else{
                        bw.write(fromId + config.getOutputFileDelimiter() + "\n");
                    }
                }
                //Include the non matched records in the delimited report if specified in the config file.
                if (anyMatches <= 0) {
                    if (config.isWriteDelimitedReport() && config.isIncludeNonMatchesInDelimitedReport())
                      bw_delimitedReport.write(LuceneUtils.buildNoMatchDelimitedString(map));
                }
            }

            // Matchers can output a report on their number of executions:
            for (Property p : config.getProperties()){
                String executionReport = p.getMatcher().getExecutionReport();
                if (executionReport != null)
                    log.debug(p.getMatcher().getExecutionReport());
            }

            bw.flush();
            bw.close();
            if (config.isWriteComparisonReport()){
                bw_report.flush();
                bw_report.close();
            }
            if (config.isWriteDelimitedReport()){
                bw_delimitedReport.flush();
                bw_delimitedReport.close();
            }
            indexWriter.close();
        } catch (Exception e) {
            log.error("Error on line : " + i + " (" + line + ")");
            e.printStackTrace();
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
                map.put(p.getName() + Configuration.ORIGINAL_SUFFIX,value);
                    // Transform the value if necessary
            for (Transformer t:p.getSourceTransformers()) {
                value = t.transform(value);
            }
            // Save into map
            map.put(p.getName(),value);
            // Save length if required
            if (p.isIndexLength()){
                int length = 0;
                if (value != null)
                    length = value.length();
                map.put(p.getName() + Configuration.LENGTH_SUFFIX,String.format("%02d", length));
            }
            if (p.isIndexInitial()){
                String init = "";
                if (StringUtils.isNotBlank(value)) init = value.substring(0,1);
                map.put(p.getName() + Configuration.INITIAL_SUFFIX, init);
            }
        }
        return map;
    }

}
