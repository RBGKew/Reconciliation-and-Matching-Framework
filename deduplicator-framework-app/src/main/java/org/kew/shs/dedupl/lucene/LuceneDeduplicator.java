package org.kew.shs.dedupl.lucene;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.shs.dedupl.Deduplicator;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;
import org.kew.shs.dedupl.configuration.Property;
import org.kew.shs.dedupl.reporters.LuceneOutputReporter;
import org.kew.shs.dedupl.reporters.LuceneOutputReporterMultiline;
import org.kew.shs.dedupl.reporters.LuceneReporter;
import org.kew.shs.dedupl.reporters.Reporter;


public class LuceneDeduplicator extends LuceneHandler implements Deduplicator {

	protected DeduplicationConfiguration dedupConfig;

	protected LuceneReporter[] reporters;

	public Reporter[] getReporters() {
		return (LuceneReporter[]) reporters;
	}

	public void setReporters(LuceneReporter[] reporters) {
		this.reporters = reporters;
	}

	public DeduplicationConfiguration getDedupConfig() {
		if (this.dedupConfig == null) {
			this.dedupConfig = (DeduplicationConfiguration) this.configuration;
		}
		return this.dedupConfig;
	}
	// TODO: generalise Configuration!

	public void loadData() throws Exception{
		this.dataLoader.setConfiguration(configuration);
		this.dataLoader.load();
	}

	public void run() throws Exception {

		this.loadData(); // writes the index according to the configuration

		// Read something
		Set<String> alreadyProcessed = new HashSet<String>();

		// TODO: implement autoclose on reporters and use then this nice try (with reporters = ..) way.
		// 	that would make explicit closing obsolete and would perform better in case sth goes wrong.
		try {
			log.debug(new java.util.Date(System.currentTimeMillis()));

			// Sort properties in order of cost:
			Collections.sort(configuration.getProperties(),  new Comparator<Property>() {
				public int compare(final Property p1,final Property p2) {
					return Integer.valueOf(p1.getMatcher().getCost()).compareTo(Integer.valueOf(
							p2.getMatcher().getCost()));
				}
			});

			// intermediate step: set the reporters here
			// TODO: define the reporters in the configuration
			DeduplicationConfiguration config = this.getDedupConfig();
			LuceneReporter outputReporter = new LuceneOutputReporter(config.getOutputFile(),
					config.getOutputFileDelimiter(), config.getScoreFieldName(), Configuration.ID_FIELD_NAME);
			// for the multiline output we (ab)use the topCopy configuration for now :-|
			LuceneReporter outputReporterMultiline = new LuceneOutputReporterMultiline(config.getTopCopyFile(),
					config.getOutputFileDelimiter(), config.getScoreFieldName(), Configuration.ID_FIELD_NAME);
			this.setReporters(new LuceneReporter[] {outputReporter, outputReporterMultiline});

			// Loop over all documents in index
			indexReader = IndexReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			int numClusters = 0;
			DocList dupls;
			for (int i=0; i<indexReader.maxDoc(); i++) {
				if (indexReader.isDeleted(i)) {
					log.error("this record id appears to be deleted in the index. why??");
					continue;
				}
				if (i % configuration.getAssessReportFrequency() == 0 || i == indexReader.maxDoc() - 1){
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
				String querystr = LuceneUtils.buildQuery(configuration.getProperties(), fromDoc, true);

				TopDocs td = queryLucene(querystr, indexSearcher);
				log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);

				StringBuffer sb = new StringBuffer();
				for (ScoreDoc sd : td.scoreDocs){
					Document toDoc = getFromLucene(sd.doc);

					log.debug(LuceneUtils.doc2String(toDoc));

					String toId = toDoc.get(Configuration.ID_FIELD_NAME);

					// Skip the processing if we have already encountered this record in the main loop
					if (alreadyProcessed.contains(toId))
						continue;

					if (LuceneUtils.recordsMatch(fromDoc, toDoc, configuration.getProperties())){
						dupls.add(toDoc);
						if (sb.length() > 0)
							sb.append(configuration.getOutputFileIdDelimiter());
						sb.append(toId);
						alreadyProcessed.add(toId);
					}
				}
				// use the DocList's specific sort method to sort on the scoreField.
				dupls.sort();
				numClusters ++;
				// call each reporter that has a say; all they get is a complete list of duplicates for this record.
				for (LuceneReporter reporter : this.reporters) {
					reporter.report(dupls);
				}
			}

			// Matchers can output a report on their number of executions:
			for (Property p : configuration.getProperties()){
				String executionReport = p.getMatcher().getExecutionReport();
				if (executionReport != null)
					log.debug(p.getMatcher().getExecutionReport());
			}
			// make all reporters finish properly (close open files, etc.)
			for (Reporter reporter : this.reporters) {
				reporter.finish();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexWriter.close();
		}

	}

}
