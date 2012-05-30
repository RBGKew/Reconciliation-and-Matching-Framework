package org.kew.shs.dedupl.lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.kew.shs.dedupl.DataLoader;
import org.kew.shs.dedupl.DataMatcher;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.MatchConfiguration;
import org.kew.shs.dedupl.configuration.Property;

/**
 * This is a Lucene implementation of the Deduplicator interface
 * @author nn00kg
 *
 */
public class LuceneMatcher implements DataMatcher{

	private org.apache.lucene.util.Version luceneVersion;
	private FSDirectory directory;
	private IndexWriter indexWriter;
	private Analyzer analyzer;
	private IndexReader indexReader;
	private QueryParser queryParser;

	private DataLoader dataLoader;
	private MatchConfiguration configuration;
	
	private static Logger log = Logger.getLogger(LuceneMatcher.class);
	
	public MatchConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(MatchConfiguration config) {
		this.configuration = config;
	}

	public DataLoader getDataLoader() {
		return dataLoader;
	}

	public void setDataLoader(DataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}

	public void loadData(){
		if (!configuration.isReuseIndex()){
			dataLoader.setConfiguration(configuration);
			dataLoader.load(configuration.getStoreFile());
		}
		else
			log.info("Reusing existing index");
	}

	public void run(){
		
		loadData();
		
		// Read something
		Set<String> alreadyProcessed = new HashSet<String>();

		int i = 0;
		String line = null;
		try {
			
			log.debug(new java.util.Date(System.currentTimeMillis()));

			IndexSearcher indexSearcher = new IndexSearcher(directory);
			indexReader = IndexReader.open(directory);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(configuration.getOutputFile()));

			BufferedWriter bw_report = null;
			if (configuration.isWriteComparisonReport())
				bw_report = new BufferedWriter(new FileWriter(configuration.getReportFile()));
			
			
			BufferedWriter bw_delimitedReport = null;
			if (configuration.isWriteDelimitedReport())
				bw_delimitedReport = new BufferedWriter(new FileWriter(configuration.getDelimitedFile()));
			
			if (configuration.isOutputAllMatches())
				log.debug("Configured to output all matches");
			else
				log.debug("Configured to only output top match");

			BufferedReader br = new BufferedReader(new FileReader(configuration.getIterateFile()));

			int numMatches = 0;
			int numColumns = LuceneDataLoader.calculateNumberColumns(configuration.getProperties());
			int anyMatches = 0;
			
			while ((line = br.readLine()) != null){
			
				if (i++ % configuration.getAssessReportFrequency() == 0)
			    	log.info("Assessed " + i + " records, found " + numMatches + " matches");
				
				Map<String,String> map = line2Map(line, numColumns);
				
				// We now have this record as a hashmap, transformed etc as the data stored in Lucene has been
				String fromId = map.get(Configuration.ID_FIELD_NAME);
			    
			    // Keep a record of the records already processed, so as not to return 
			    // matches like id1:id2 *and* id2:id1
			    alreadyProcessed.add(fromId);
				
			    // Use the properties to select a set of documents which may contain matches
				String querystr = LuceneUtils.buildQuery(configuration.getProperties(), map, false);

				TopDocs td = queryLucene(querystr, indexSearcher);
				log.debug("Found " + td.totalHits + " possibles to assess against " + fromId);
				
				anyMatches = 0;				

				SortedMap<Object,String> matches = null;
				if (configuration.isOutputAllMatches()){
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

					if (LuceneUtils.recordsMatch(map, toDoc, configuration.getProperties())){
						numMatches++;
						anyMatches++;
						if (configuration.isOutputAllMatches()){
							matches.put(toId, toId);
						}
						else{
							String score = toDoc.get(configuration.getScoreField());
							log.debug("Match score: " + Integer.valueOf(score));
							matches.put(Integer.valueOf(score), toId);
						}
						if (configuration.isWriteComparisonReport()){
							bw_report.write(fromId + configuration.getOutputFileDelimiter() + toId + "\n");
							bw_report.write(LuceneUtils.buildComparisonString(configuration.getProperties(), map, toDoc));
						}
						if (configuration.isWriteDelimitedReport()){
							bw_delimitedReport.write(LuceneUtils.buildFullComparisonString(map, toDoc));
						}						
					}
				}
				if (configuration.isOutputAllMatches()){
					StringBuffer sb = new StringBuffer();
					for (String id : matches.values()){
						if (sb.length() > 0)
							sb.append(",");
						sb.append(id);
					}
					bw.write(fromId + configuration.getOutputFileDelimiter() + sb.toString() + "\n");
				}
				else{
					// only output the highest scoring match
					String bestMatchId = null;
					if (!matches.isEmpty()){
						log.debug("Number of matches: " + matches.size() + ", Last key: " + matches.lastKey());
						bestMatchId = matches.get(matches.lastKey());
						bw.write(fromId + configuration.getOutputFileDelimiter() + bestMatchId + "\n");
					}
					else{
						bw.write(fromId + configuration.getOutputFileDelimiter() + "\n");
					}
				}
				//Include the non matched records in the delimited report if specified in the config file.
				if (anyMatches <= 0) {
					if (configuration.isWriteDelimitedReport() && configuration.isIncludeNonMatchesInDelimitedReport())
					  bw_delimitedReport.write(LuceneUtils.buildNoMatchDelimitedString(map));	
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
			if (configuration.isWriteComparisonReport()){
				bw_report.flush();
				bw_report.close();
			}
			if (configuration.isWriteDelimitedReport()){
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
		String[] elem = line.split(configuration.getInputFileDelimiter(), numColumns+1);
		map.put(Configuration.ID_FIELD_NAME, elem[0]);
		for (Property p : configuration.getProperties()){
			String value = elem[p.getColumnIndex()];
			// Save original value if required
			if (p.isIndexOriginal())
				map.put(p.getName() + Configuration.ORIGINAL_SUFFIX,value);
			// Transform if required
			if (p.getTransformer()!=null)
				value = p.getTransformer().transform(value);
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
	
	public Document getFromLucene(int n) throws IOException{
		return indexReader.document(n);
	}

	public TopDocs queryLucene(String query, IndexSearcher indexSearcher) throws IOException, ParseException {
		log.debug(query);
		Query q = queryParser.parse(query);
		log.debug(q);
		return indexSearcher.search(q, 1000);
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