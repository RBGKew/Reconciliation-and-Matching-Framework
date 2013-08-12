package org.kew.shs.dedupl.lucene;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.shs.dedupl.DataHandler;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;
import org.kew.shs.dedupl.configuration.Property;
import org.kew.shs.dedupl.reporters.LuceneReporter;
import org.kew.shs.dedupl.reporters.Piper;


public class LuceneDeduplicator extends LuceneHandler<DeduplicationConfiguration> implements DataHandler<DeduplicationConfiguration> {

    protected DeduplicationConfiguration dedupConfig;

    public void loadData() throws Exception {
        if (!getConfig().isReuseIndex()){
            dataLoader.setConfig(this.getConfig());
            dataLoader.load();
        }
    }

    public void run() throws Exception {

        this.loadData(); // writes the index according to the configuration

        Set<String> alreadyProcessed = new HashSet<>();

        try (DeduplicationConfiguration config = this.getConfig();
             IndexWriter indexWriter = this.indexWriter) {

            this.prepareEnvs();

            // Loop over all documents in index
            int numClusters = 0;
            DocList dupls;
            for (int i=0; i<this.getIndexReader().maxDoc(); i++) {
                if (i % config.getAssessReportFrequency() == 0 || i == this.getIndexReader().maxDoc() - 1){
                    logger.info("Assessed {} records, merged to {} duplicate clusters", i, numClusters);
                }

                Document fromDoc = getFromLucene(i);

                Map<String, String> docAsMap = LuceneUtils.doc2Map(fromDoc);
                // pipe everything through to the output where an existing filter evals to false;
                if (!StringUtils.isBlank(config.getRecordFilter()) && !jsEnv.evalFilter(config.getRecordFilter(), docAsMap)) {
                    for (Piper piper:config.getPipers()) piper.pipe(docAsMap);
                    continue;
                }

                dupls = new DocList(fromDoc, config.getScoreFieldName()); // each fromDoc has a duplicates cluster

                logger.debug(LuceneUtils.doc2String(fromDoc));

                String fromId = fromDoc.get(Configuration.ID_FIELD_NAME);
                // Keep a record of the records already processed, so as not to return
                // matches like id1:id2 *and* id2:id1
                if (alreadyProcessed.contains(fromId))
                    continue;
                alreadyProcessed.add(fromId);

                // Use the properties to select a set of documents which may contain matches
                String querystr = LuceneUtils.buildQuery(config.getProperties(), fromDoc, true);
                // If the query for some reasons results being empty we pipe the record directly through to the output
                // TODO: create a log-file that stores critical log messages?
                if (querystr.equals("")) {
                    logger.warn("Empty query for record {}", fromDoc);
                    for (Piper piper:config.getPipers()) piper.pipe(docAsMap);
                    continue;
                }

                TopDocs td = queryLucene(querystr, this.getIndexSearcher(), config.getMaxSearchResults());
                if (td.totalHits == config.getMaxSearchResults()) {
                    throw new Exception(String.format("Number of max search results exceeded for record %s! You should either tweak your config to bring back less possible results making better use of the \"useInSelect\" switch (recommended) or raise the \"maxSearchResults\" number.", fromDoc));
                }
                logger.debug("Found {} possibles to assess against {}", td.totalHits, fromId);

                for (ScoreDoc sd : td.scoreDocs){
                    Document toDoc = getFromLucene(sd.doc);
                    logger.debug(LuceneUtils.doc2String(toDoc));

                    String toId = toDoc.get(Configuration.ID_FIELD_NAME);
                    // Skip the processing if we have already encountered this record in the main loop
                    if (alreadyProcessed.contains(toId))
                        continue;

                    if (LuceneUtils.recordsMatch(fromDoc, toDoc, config.getProperties())){
                        dupls.add(toDoc);
                        alreadyProcessed.add(toId);
                    }
                }
                // use the DocList's specific sort method to sort on the scoreField.
                try {
                    dupls.sort();
                } catch (Exception e) {
                    throw new Exception ("Could not sort on score-field:" + e.toString());
                }
                numClusters ++;
                // call each reporter that has a say; all they get is a complete list of duplicates for this record.
                for (LuceneReporter reporter : config.getReporters()) {
                    // TODO: make idFieldName configurable, but not on reporter level
                    reporter.setIdFieldName(Configuration.ID_FIELD_NAME);
                    reporter.setDefinedOutputFields(config.outputDefs());
                    reporter.report(dupls);
                }
            }

            // Matchers can output a report on their number of executions:
            for (Property p : config.getProperties()){
                String executionReport = p.getMatcher().getExecutionReport();
                if (executionReport != null)
                    logger.debug(p.getMatcher().getExecutionReport());
            }
        }

    }

}
