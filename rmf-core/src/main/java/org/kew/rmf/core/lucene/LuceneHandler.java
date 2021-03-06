/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.core.lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kew.rmf.core.DataLoader;
import org.kew.rmf.core.configuration.Configuration;
import org.kew.rmf.core.script.JavaScriptEnv;
import org.kew.rmf.reporters.LuceneReporter;
import org.kew.rmf.reporters.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneHandler<C extends Configuration> {
	private static final Logger logger = LoggerFactory.getLogger(LuceneHandler.class);

	private org.apache.lucene.util.Version luceneVersion;

	protected FSDirectory directory;
	private IndexSearcher indexSearcher;
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
		if (this.indexSearcher == null) {
			this.indexSearcher = new IndexSearcher(this.getIndexReader());
		}
		return this.indexSearcher;
	}

	public Document getFromLucene(int n) throws IOException {
		return indexReader.document(n);
	}

	/**
	 * Retrieves a record by indexed id from the Lucene datastore.
	 */
	public Map<String, String> getRecordById(String id) throws IOException {
		String queryString = Configuration.ID_FIELD_NAME + ":" + QueryParserUtil.escape(id);

		TopDocs resultDoc = null;
		try {
			resultDoc = queryLucene(queryString, getIndexSearcher(), 1);
		}
		catch (ParseException e) {
			logger.error("Unexpected error parsing query '"+queryString+"'", e);
			return null;
		}

		if (resultDoc.scoreDocs.length > 0) {
			return LuceneUtils.doc2Map(getFromLucene(resultDoc.scoreDocs[0].doc));
		}
		else {
			return new HashMap<>();
		}
	}

	public TopDocs queryLucene(String query, IndexSearcher indexSearcher, int maxSearchResults) throws IOException, ParseException {
		Query q = queryParser.parse(query);
		logger.debug("Querying Lucene with query --> {}", q);
		TopDocs td = indexSearcher.search(q, maxSearchResults);
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
