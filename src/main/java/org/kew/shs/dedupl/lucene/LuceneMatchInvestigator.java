package org.kew.shs.dedupl.lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kew.shs.dedupl.Configuration;
import org.kew.shs.dedupl.MatchInvestigator;

public class LuceneMatchInvestigator implements MatchInvestigator {

	
	private Configuration configuration;

	private org.apache.lucene.util.Version luceneVersion;
	private IndexSearcher indexSearcher;
	private IndexReader reader;
	private FSDirectory directory;

	private QueryParser queryParser;

	public void run() {
		if (configuration.isWriteComparisonReport()){
			doComparisonReport();
		}
	}

	private void doComparisonReport(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(configuration.getOutputFile()));
			
			// Open the specified report file for writing 
			BufferedWriter bw = new BufferedWriter(new FileWriter(configuration.getReportFile()));
	
			// Set up the query parser
			reader = IndexReader.open(directory);
			
			String line = null;
			while ((line = br.readLine()) != null){
				String id1 = line.split(configuration.getOutputFileDelimiter())[0];
				String id2 = line.split(configuration.getOutputFileDelimiter())[1];
				Document doc1 = getDocumentById(id1);
				Document doc2 = getDocumentById(id2);
				bw.write(line + "\n");
				bw.write(LuceneUtils.docs2ComparisonString(doc1, doc2));
			}
			bw.flush();
			bw.close();
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private Document getDocumentById(String id){
		Document d = null;
		try{
			Query q = queryParser.parse("id:"+id);
			TopDocs td = indexSearcher.search(q,1);
			int docId = td.scoreDocs[0].doc;
			d = reader.document(docId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return d;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
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