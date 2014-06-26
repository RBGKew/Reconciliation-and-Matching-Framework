package org.kew.stringmod.dedupl.lucene;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kew.stringmod.dedupl.DataLoader;
import org.kew.stringmod.dedupl.configuration.Configuration;
import org.kew.stringmod.dedupl.reporters.LuceneReporter;
import org.kew.stringmod.dedupl.reporters.Reporter;
import org.kew.stringmod.dedupl.script.JavaScriptEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneHandler<C extends Configuration> {
	private static final Logger logger = LoggerFactory.getLogger(LuceneHandler.class);

	private org.apache.lucene.util.Version luceneVersion;

	protected FSDirectory directory;
	private IndexSearcher indexSearcher;
	protected IndexWriter indexWriter;
	private IndexReader indexReader;
	private QueryParser queryParser;
	protected DataLoader dataLoader;
	protected C config;
	protected LuceneReporter[] reporters;
	protected JavaScriptEnv jsEnv = null;

	public IndexReader getIndexReader() throws CorruptIndexException, IOException {
		if (this.indexReader == null) {
			this.indexReader = DirectoryReader.open(this.directory);
		}
		return this.indexReader;
	}

	public IndexSearcher getIndexSearcher() throws CorruptIndexException, IOException {
		if (this.indexSearcher == null) this.indexSearcher = new IndexSearcher(this.getIndexReader());
		return this.indexSearcher;
	}

	public Document getFromLucene(int n) throws IOException {
		return indexReader.document(n);
	}

	public TopDocs queryLucene(String query, IndexSearcher indexSearcher, int maxSearchResults) throws IOException, ParseException {
		Query q = queryParser.parse(query);
		logger.debug("Querying Lucene with query --> {}", q);
		TopDocs td = indexSearcher.search(q, maxSearchResults);
		logger.debug(Integer.toString(td.totalHits));
		return td;
	}
	
	/**
	 * Asks the config to initialise the Reporters properly and initialises the in-the-box
	 * javascript environment, if it will be used.
	 */
    public void prepareEnvs() {
        // copy some necessary values to reporters and possibly create pipers if recordFilter is set
        config.setupReporting();
        // the recordFilter (excludes records from processing if condition fulfilled) needs a JavaScript environment set up
        if (!StringUtils.isBlank(config.getRecordFilter())) {
            this.jsEnv = new JavaScriptEnv();
            logger.debug("Record filter activated, javascript rock'n roll!");
        }
    }

	// Getters and Setters
	public C getConfig() {
		return this.config;
	}
	public void setConfig(C config) {
		this.config = config;
	}

	public DataLoader getDataLoader() {
		return dataLoader;
	}
	public void setDataLoader(DataLoader dataLoader) {
		this.dataLoader = dataLoader;
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

	public QueryParser getQueryParser() {
		return queryParser;
	}
	public void setQueryParser(QueryParser queryParser) {
		this.queryParser = queryParser;
	}

	public Reporter[] getReporters() {
		return (LuceneReporter[]) reporters;
	}
	public void setReporters(LuceneReporter[] reporters) {
		this.reporters = reporters;
	}
}
