package org.kew.shs.dedupl.lucene;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
import org.kew.shs.dedupl.Investigator;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.Property;

/**
 * This is a Lucene implementation of the Deduplicator interface
 * @author nn00kg
 *
 */
public class LuceneDeduplicator extends LuceneHandler implements Deduplicator {

	private Investigator investigator;
	
	// TODO: generalise Configuration!

	public void loadData(){
		dataLoader.setConfiguration(configuration);
		dataLoader.load();
	}

	public void run(){

		loadData(); // writes the index according to the configuration

		// Read something
		Set<String> alreadyProcessed = new HashSet<String>();

		try {
			log.debug(new java.util.Date(System.currentTimeMillis()));

			IndexSearcher indexSearcher = new IndexSearcher(directory);

			// Sort properties in order of cost:
			Collections.sort(configuration.getProperties(),  new Comparator<Property>() {
		        public int compare(final Property p1,final Property p2) {
		        	return Integer.valueOf(p1.getMatcher().getCost()).compareTo(Integer.valueOf(p2.getMatcher().getCost()));
		        }
		    });

			// Open the specified output file for writing
			BufferedWriter bw = new BufferedWriter(new FileWriter(configuration.getOutputFile()));

			// Loop over all documents in index
			indexReader = IndexReader.open(directory);
			int numMatches = 0;
			for (int i=0; i<indexReader.maxDoc(); i++) {
				if (indexReader.isDeleted(i))
					continue;
				if (i % configuration.getAssessReportFrequency() == 0){
					log.info("Assessed " + i + " records, merged to " + (i - numMatches) + " duplicate clusters");
					bw.flush();
				}

			    Document fromDoc = getFromLucene(i);

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
						numMatches++;
						if (sb.length() > 0)
							sb.append(configuration.getOutputFileIdDelimiter());
						sb.append(toId);
						alreadyProcessed.add(toId);
					}
				}
				bw.write(fromId + configuration.getOutputFileDelimiter() + sb.toString() + "\n");
			}

			// Matchers can output a report on their number of executions:
			for (Property p : configuration.getProperties()){
				String executionReport = p.getMatcher().getExecutionReport();
				if (executionReport != null)
					log.debug(p.getMatcher().getExecutionReport());
			}

			bw.flush();
			bw.close();
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Investigator getInvestigator() {
		return investigator;
	}

	public void setInvestigator(Investigator investigator) {
		this.investigator = investigator;
	}

}
