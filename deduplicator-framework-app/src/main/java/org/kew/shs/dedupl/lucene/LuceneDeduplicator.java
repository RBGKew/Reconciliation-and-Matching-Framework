package org.kew.shs.dedupl.lucene;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.shs.dedupl.DataHandler;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;
import org.kew.shs.dedupl.configuration.Property;
import org.kew.shs.dedupl.reporters.LuceneReporter;


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

		// TODO: implement autoclose on reporters and use then this nice try (with reporters = ..) way.
		// 	that would make explicit closing obsolete and would perform better in case sth goes wrong.
		try (DeduplicationConfiguration config = this.getConfig();
		     IndexWriter indexWriter = this.indexWriter) {

			log.debug(new java.util.Date(System.currentTimeMillis()));

			// Sort properties in order of cost:
			Collections.sort(config.getProperties(),  new Comparator<Property>() {
				public int compare(final Property p1,final Property p2) {
					return Integer.valueOf(p1.getMatcher().getCost()).compareTo(Integer.valueOf(
							p2.getMatcher().getCost()));
				}
			});
			// Loop over all documents in index
			int numClusters = 0;
			DocList dupls;
			for (int i=0; i<this.getIndexReader().maxDoc(); i++) {
				if (this.getIndexReader().isDeleted(i)) {
					log.error("this record id appears to be deleted in the index. why??");
					continue;
				}
				if (i % config.getAssessReportFrequency() == 0 || i == this.getIndexReader().maxDoc() - 1){
					log.info("Assessed " + i + " records, merged to " + (numClusters) + " duplicate clusters");
				}

				Document fromDoc = getFromLucene(i);
				dupls = new DocList(fromDoc, config.getScoreFieldName()); // each fromDoc has a duplicates cluster

				log.debug(LuceneUtils.doc2String(fromDoc));

				String fromId = fromDoc.get(Configuration.ID_FIELD_NAME);

				// Keep a record of the records already processed, so as not to return
				// matches like id1:id2 *and* id2:id1
				if (alreadyProcessed.contains(fromId))
					continue;
				alreadyProcessed.add(fromId);

				// Use the properties to select a set of documents which may contain matches
				String querystr = LuceneUtils.buildQuery(config.getProperties(), fromDoc, true);

				TopDocs td = queryLucene(querystr, this.getIndexSearcher());
				log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);

				for (ScoreDoc sd : td.scoreDocs){
					Document toDoc = getFromLucene(sd.doc);
					log.debug(LuceneUtils.doc2String(toDoc));

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
				dupls.sort();
				numClusters ++;
				// call each reporter that has a say; all they get is a complete list of duplicates for this record.
				for (LuceneReporter reporter : config.getReporters()) {
					// TODO: make idFieldName configurable, but not on reporter level
					reporter.setIdFieldName(Configuration.ID_FIELD_NAME);
					reporter.report(dupls);
				}
			}

			// Matchers can output a report on their number of executions:
			for (Property p : config.getProperties()){
				String executionReport = p.getMatcher().getExecutionReport();
				if (executionReport != null)
					log.debug(p.getMatcher().getExecutionReport());
			}
		}

	}

}
