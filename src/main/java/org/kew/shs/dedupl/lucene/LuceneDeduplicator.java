package org.kew.shs.dedupl.lucene;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kew.shs.dedupl.Configuration;
import org.kew.shs.dedupl.Deduplicator;
import org.kew.shs.dedupl.Property;

/**
 * This is a Lucene implementation of the Deduplicator interface
 * @author nn00kg
 *
 */
public class LuceneDeduplicator implements Deduplicator{

	private org.apache.lucene.util.Version luceneVersion;
	private FSDirectory directory;

	private IndexSearcher indexSearcher;
	private IndexWriter indexWriter;
	private Analyzer analyzer;

	private Configuration configuration;
	
	private IndexReader indexReader;
	private QueryParser queryParser;
	
	private static Logger log = Logger.getLogger(LuceneDeduplicator.class);
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration config) {
		this.configuration = config;
	}

	public void run(){
		// Read something
		Set<String> alreadyProcessed = new HashSet<String>();
		
		try {
			log.debug(new java.util.Date(System.currentTimeMillis()));
			
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
			    	log.info("Assessed " + i + " records, found " + numMatches + " duplicates");
			    	bw.flush();
			    }

			    Document fromDoc = getFromLucene(i);
			    
			    log.debug(LuceneUtils.doc2String(fromDoc));
			    
			    String fromId = fromDoc.get(Configuration.ID_FIELD_NAME);
			    
			    // Keep a record of the records already processed, so as not to return 
			    // matches like id1:id2 *and* id2:id1
			    alreadyProcessed.add(fromId);
			    
			    // Use the properties to select a set of documents which may contain matches
				String querystr = buildQuery(configuration.getProperties(), fromDoc);

				TopDocs td = queryLucene(querystr);
				log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);
				
				for (ScoreDoc sd : td.scoreDocs){
					Document toDoc = getFromLucene(sd.doc);
					
					log.debug(LuceneUtils.doc2String(toDoc));
					
					String toId = toDoc.get(Configuration.ID_FIELD_NAME);
					
					// Skip the processing if we have already encountered this record in the main loop
				    if (alreadyProcessed.contains(toId))
				    	continue;

					boolean match = false;
					for (Property p : configuration.getProperties()){
						String s1 = fromDoc.get(p.getName());
						String s2 = toDoc.get(p.getName());
						
						String[] s = new String[2];
						s[0] = s1;
						s[1] = s2;
						Arrays.sort(s);
						match = p.getMatcher().matches(s[0], s[1]);
						
						log.debug(s[0] + " : " + s[1] + " : " + match);

						if (!match){
							if (p.isBlanksMatch()){
								match = (StringUtils.isBlank(s1) || StringUtils.isBlank(s2)); 
								log.debug(p.getName() + ": blanks match");
							}
							else{
								log.debug("failed on " + p.getName());
								break;
							}
						}
					}
					if (match){
						numMatches++;
						bw.write(fromId + configuration.getOutputFileDelimiter() + toId + "\n");
						//if (config.isWriteComparisonReport())
						//	bw.write(LuceneUtils.docs2ComparisonString(fromDoc, toDoc, "#"));
					}
				}
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

	public Document getFromLucene(int n) throws IOException{
		return indexReader.document(n);
	}

	public TopDocs queryLucene(String query) throws IOException, ParseException {
		Query q = queryParser.parse(query);
		return indexSearcher.search(q, 1000);
	}

	private String buildQuery(List<Property> properties, Document doc){
		StringBuffer sb = new StringBuffer();
		// Be sure not to return self:
		sb.append("NOT " + Configuration.ID_FIELD_NAME + ":" + doc.get(Configuration.ID_FIELD_NAME));
		for (Property p : properties){
			if (p.isUseInSelect()){
				String value = doc.get(p.getName());
				if (StringUtils.isNotBlank(value)){
					if (sb.length() > 0) sb.append(" AND ");
					if(p.getMatcher().isExact())
						sb.append(p.getName() + ":" + value);
					if (p.isIndexLength()){
						/** todo: remove these magic numbers */ 
						sb.append(p.getName() + Configuration.LENGTH_SUFFIX + ":[").append(value.length() - 2).append(" TO ").append(value.length() + 2).append("]");
					}
					if (p.isIndexInitial()){
						sb.append(" AND ")
							.append(p.getName() + Configuration.INITIAL_SUFFIX).append(":").append(value.substring(0, 1));
					}
				}
			}
		}
		//log.info(sb.toString());
		return sb.toString();
	}

	public org.apache.lucene.util.Version getLuceneVersion() {
		return luceneVersion;
	}

	public void setLuceneVersion(org.apache.lucene.util.Version luceneVersion) {
		this.luceneVersion = luceneVersion;
	}

	public FSDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(FSDirectory directory) {
		this.directory = directory;
	}

	public IndexSearcher getIndexSearcher() {
		return indexSearcher;
	}

	public void setIndexSearcher(IndexSearcher indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public void setIndexWriter(IndexWriter indexWriter) {
		this.indexWriter = indexWriter;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Configuration getConfig() {
		return configuration;
	}

	public void setConfig(Configuration config) {
		this.configuration = config;
	}

	public IndexReader getIndexReader() {
		return indexReader;
	}

	public void setIndexReader(IndexReader indexReader) {
		this.indexReader = indexReader;
	}

	public QueryParser getQueryParser() {
		return queryParser;
	}

	public void setQueryParser(QueryParser queryParser) {
		this.queryParser = queryParser;
	}
	
}