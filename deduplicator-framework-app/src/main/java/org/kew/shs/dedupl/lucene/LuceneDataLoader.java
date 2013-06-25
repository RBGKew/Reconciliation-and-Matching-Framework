package org.kew.shs.dedupl.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.kew.shs.dedupl.DataLoader;
import org.kew.shs.dedupl.configuration.Configuration;
import org.kew.shs.dedupl.configuration.Property;

/**
 * This is a Lucene implementation of the DataLoader interface
 * @author nn00kg
 *
 */
public class LuceneDataLoader implements DataLoader{

	private org.apache.lucene.util.Version luceneVersion;
	private IndexWriter indexWriter;
	private FSDirectory directory;

	private Analyzer analyzer;

	private Configuration configuration;

	private static Logger log = Logger.getLogger(LuceneDataLoader.class);

	public void load() throws Exception{
		load(configuration.getInputFile());
	}

	public void load(File file) throws Exception{
		int i = 0;
		String encoding = configuration.getInputFileEncoding();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
			// Read from the specified input file
			String line = null;
			int numColumns = calculateNumberColumns(configuration.getProperties());
			if (configuration.isInputFileIgnoreHeader())
				br.readLine();
			while ((line = br.readLine()) != null){
				Document doc = new Document();
				String[] elem = line.split(configuration.getInputFileDelimiter());
//				String[] elem = line.split(configuration.getInputFileDelimiter(), numColumns+1);
				log.debug(Arrays.toString(elem));
				// The first element is always the ID
				doc.add(new Field(Configuration.ID_FIELD_NAME, elem[0], Field.Store.YES,Field.Index.ANALYZED));
				//doc.add(new Field("processed", "false", Field.Store.YES,Field.Index.ANALYZED));
				// The remainder of the columns are added as specified in the properties
				for (Property p : configuration.getProperties()){
					String value = elem[p.getColumnIndex()];

					if (p.isIndexOriginal()){
						// Index value in its original state, pre transformation
						Field f1 = new Field(p.getName() + Configuration.ORIGINAL_SUFFIX, value, Field.Store.YES,Field.Index.ANALYZED);
						doc.add(f1);
					}

					// Transform the value if necessary
					if (p.getTransformer()!=null)
						value = p.getTransformer().transform(value);
					Field f = new Field(p.getName(), value, Field.Store.YES,Field.Index.ANALYZED);
					doc.add(f);

					// For some fields (those which will be passed into a fuzzy matcher like
					// Levenshtein), we index the length
					if (p.isIndexLength()){
						int length = 0;
						if (value != null)
							length = value.length();
						Field fl = new Field(p.getName() + Configuration.LENGTH_SUFFIX, String.format("%02d", length), Field.Store.YES,Field.Index.ANALYZED);
						doc.add(fl);
					}
					if (p.isIndexInitial() & StringUtils.isNotBlank(value)){
						Field finit = new Field(p.getName() + Configuration.INITIAL_SUFFIX, value.substring(0, 1), Field.Store.YES,Field.Index.ANALYZED);
						doc.add(finit);
					}
				}
				indexWriter.addDocument(doc);
				if (i++ % configuration.getLoadReportFrequency() == 0)
					log.info("Indexed " + i + " documents");
			}
			indexWriter.commit();
		}
		catch(Exception e){
			throw e;
		}
		log.info("Indexed " + i + " documents");
	}

	public static int calculateNumberColumns(List<Property> properties){
		int numColumns = 0;
		for (Property p : properties){
			if (p.getColumnIndex() > numColumns)
				numColumns = p.getColumnIndex();
		}
		return numColumns;
	}

	public org.apache.lucene.util.Version getLuceneVersion() {
		return luceneVersion;
	}

	public void setLuceneVersion(org.apache.lucene.util.Version luceneVersion) {
		this.luceneVersion = luceneVersion;
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public void setIndexWriter(IndexWriter indexWriter) {
		this.indexWriter = indexWriter;
	}

	public FSDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(FSDirectory directory) {
		this.directory = directory;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
