package org.kew.shs.dedupl.lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kew.shs.dedupl.Investigator;
import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;

public class LuceneDeduplicatorInvestigator implements Investigator {

	private static Logger log = Logger.getLogger(LuceneDeduplicatorInvestigator.class);
	
	private DeduplicationConfiguration configuration;

	private org.apache.lucene.util.Version luceneVersion;
	private IndexSearcher indexSearcher;
	private IndexReader reader;
	private FSDirectory directory;

	private QueryParser queryParser;

	public void run() {
		if (configuration.isWriteComparisonReport())
			doComparisonReport();
		if (configuration.isWriteTopCopyReport())
			doTopCopyReport(configuration.getScoreFieldName());
	}

	private int num_tied_scores = 0;
	
	private void doTopCopyReport(String scoreFieldName){
		try{
			indexSearcher = new IndexSearcher(directory);
			
			BufferedReader br = new BufferedReader(new FileReader(configuration.getOutputFile()));
			
			// Open the specified report file for writing 
			BufferedWriter bw = new BufferedWriter(new FileWriter(configuration.getTopCopyFile()));
	
			// Set up the query parser
			reader = IndexReader.open(directory);
			
			String line = null;
			while ((line = br.readLine()) != null){
				List<String> ids = new ArrayList<String>(); 
				String from_id = line.split(configuration.getOutputFileDelimiter())[0];
				ids.add(from_id);
				if (line.split(configuration.getOutputFileDelimiter()).length > 1){
					String[] to_ids = line.split(configuration.getOutputFileDelimiter())[1].split(configuration.getOutputFileIdDelimiter());
					for (String to_id : to_ids)
						ids.add(to_id);
					if (ids.size() > 0){
						String querystr = idsToQuery(ids);
						TopDocs td = queryLucene(querystr, indexSearcher,scoreFieldName);
						List<Document> dupls = new ArrayList<Document>();
						for (ScoreDoc sd : td.scoreDocs){
							Document doc = getFromLucene(sd.doc);
							dupls.add(doc);
						}
						bw.write(LuceneUtils.doc2Line(selectBestDocument(dupls, scoreFieldName),configuration.getOutputFileDelimiter()));
					}
				}
				else{
					Document doc = getDocumentById(from_id);
					bw.write(LuceneUtils.doc2Line(doc, configuration.getOutputFileDelimiter()));
				}
			}
			log.debug("Number of clusters with tied scores (i.e. an arbitrary choice re best record was made): " + num_tied_scores);
			bw.flush();
			bw.close();
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Document selectBestDocument(List<Document> docs, String scoreFieldName){
		Document doc = null;
		if (docs != null){
			doc = docs.get(0);
			if (docs.size() > 1){
				if (doc.get(scoreFieldName).equals(docs.get(1).get(scoreFieldName))){
					num_tied_scores++;
				}
			}
		}
		return doc; 
	}
	
	private String idsToQuery(List<String> ids){
		String query = null;
		StringBuffer sbids = new StringBuffer();
		if (ids != null){
			for (String id : ids){
				if (sbids.length() > 0) sbids.append(" OR ");
				sbids.append(id);
			}
		}
		if (sbids.length() > 0)
			query = "id:(" + sbids.toString() + ")";
		return query;
	}
	
	private void doComparisonReport(){
		try{
			indexSearcher = new IndexSearcher(directory);
			
			BufferedReader br = new BufferedReader(new FileReader(configuration.getOutputFile()));
			
			// Open the specified report file for writing 
			BufferedWriter bw = new BufferedWriter(new FileWriter(configuration.getReportFile()));
	
			// Set up the query parser
			reader = IndexReader.open(directory);
			
			String line = null;
			while ((line = br.readLine()) != null){
				String from_id = line.split(configuration.getOutputFileDelimiter())[0];
				if (line.split(configuration.getOutputFileDelimiter()).length > 1){
					Document from = getDocumentById(from_id);
					String[] to_ids = line.split(configuration.getOutputFileDelimiter())[1].split(configuration.getOutputFileIdDelimiter());
					for (String to_id : to_ids){
						Document to = getDocumentById(to_id);
						bw.write(line + "\n");
						bw.write(LuceneUtils.buildComparisonString(configuration.getProperties(), from, to));					
					}
				}
			}
			bw.flush();
			bw.close();
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public TopDocs queryLucene(String query, IndexSearcher indexSearcher, String scoreFieldName) throws IOException, ParseException {
		Query q = queryParser.parse(query);
		return indexSearcher.search(q, 6, new Sort(new SortField(scoreFieldName, SortField.INT, true)));
	}

	public Document getFromLucene(int n) throws IOException{
		return reader.document(n);
	}
	
	private Document getDocumentById(String id){
		Document d = null;
		try{
			Query q = queryParser.parse("id:"+id.replace("\\-", "\\\\-"));
			TopDocs td = indexSearcher.search(q,1);
			int docId = td.scoreDocs[0].doc;
			d = reader.document(docId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return d;
	}

	public DeduplicationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(DeduplicationConfiguration configuration) {
		this.configuration = configuration;
	}

	public org.apache.lucene.util.Version getLuceneVersion() {
		return luceneVersion;
	}

	public void setLuceneVersion(org.apache.lucene.util.Version luceneVersion) {
		this.luceneVersion = luceneVersion;
	}

	public IndexSearcher getIndexSearcher() {
		return indexSearcher;
	}

	public void setIndexSearcher(IndexSearcher indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	public IndexReader getReader() {
		return reader;
	}

	public void setReader(IndexReader reader) {
		this.reader = reader;
	}

	public FSDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(FSDirectory directory) {
		this.directory = directory;
	}

	public QueryParser getQueryParser() {
		return queryParser;
	}

	public void setQueryParser(QueryParser queryParser) {
		this.queryParser = queryParser;
	}
	
}