package org.kew.stringmod.dedupl.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.kew.stringmod.dedupl.DataLoader;
import org.kew.stringmod.dedupl.DatabaseRecordSource;
import org.kew.stringmod.dedupl.configuration.Configuration;
import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.exception.DataLoadException;
import org.kew.stringmod.lib.transformers.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.base.Strings;

/**
 * This implementation reads a file and stores its content in a Lucene index.
 * The rules for this are defined in the corresponding Configuration.
 */
public class LuceneDataLoader implements DataLoader {

	/**
	 * RAM buffer to use for loading data into Lucene.
	 */
	private static final double RAM_BUFFER_SIZE = 64;

	private org.apache.lucene.util.Version luceneVersion;

	/**
	 * The IndexWriter is set in {@link #openIndex()}, and closed at the end of {@link #load()}.
	 */
	private IndexWriter indexWriter;

    private FSDirectory directory;

	private Analyzer luceneAnalyzer;

    private Configuration config;
	private String configName;

    private static Logger logger = LoggerFactory.getLogger(LuceneDataLoader.class);

	/**
	 * Load the authority data into Lucene.
	 */
	@Override
	public void load() throws DataLoadException {
		try {
			indexWriter = openIndex();

			if (getConfig().isReuseIndex()) {
				logger.warn("{}: Reuse index not yet implemented, rereading", configName);
			}

			/*
			 * In case no file is specified we (assuming a de-duplication task) use the
			 * queryFile also for index creation.
			 *
			 * This is why we copy over the query-related properties to the
			 * authority-related ones
			 */
			if (config.getAuthorityRecords() == null) {
				if (config.getAuthorityFile() == null) {
					for (Property p : config.getProperties()) {
						p.setAuthorityTransformers(p.getQueryTransformers());
						p.setAddOriginalAuthorityValue(p.isAddOriginalQueryValue());
						p.setAddTransformedAuthorityValue(p.isAddTransformedQueryValue());
						p.setAuthorityColumnName(p.getQueryColumnName());
					}
					config.setAuthorityFile(config.getQueryFile());
					config.setAuthorityFileEncoding(config.getQueryFileEncoding());
					config.setAuthorityFileDelimiter(config.getQueryFileDelimiter());
				}
				this.load(config.getAuthorityFile());
			}
			else {
				logger.info("{}: Starting data load", configName);
				this.load(config.getAuthorityRecords());
			}
		}
		catch (IOException e) {
			throw new DataLoadException(configName + ": Problem creating/opening Lucene index", e);
		}
		finally {
			// Close the index, no matter what happened.
			try {
				if (indexWriter != null) {
					indexWriter.close();
					logger.info("{}: IndexWriter {} closed", configName, indexWriter.getDirectory());
				}
			}
			catch (IOException e) {
				throw new DataLoadException(configName + ": Error closing Lucene index for "+configName, e);
			}
		}
	}

	/**
	 * Opens an IndexWriter, reusing or wiping an existing index according to the configuration.
	 */
	private IndexWriter openIndex() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(getLuceneVersion(), luceneAnalyzer);
		indexWriterConfig.setRAMBufferSizeMB(RAM_BUFFER_SIZE);

//		if (getConfig().isReuseIndex()) {
//			// Reuse the index if it exists, otherwise create a new one.
//			logger.debug("{}: Reusing existing index, if it exists", configName);
//			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//		}
//		else {
			// Create a new index, overwriting any that already exists.
			logger.debug("{}: Wiping existing index, if it exists", configName);
			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//		}

		return new IndexWriter(directory, indexWriterConfig);
	}

	private void load(DatabaseRecordSource recordSource) throws DataLoadException {
        int i = 0;
        int errors = 0;

        ResultSet resultSet;
        try {
            resultSet = recordSource.getResultSet();
        }
        catch (SQLException e) {
            throw new DataLoadException(configName + ": Problem reading data from database "+recordSource, e);
        }

        try {
            // check whether the necessary column names are present in the ResultSet
            for (String headerName : this.config.getPropertyAuthorityColumnNames()) {
                try {
                    resultSet.findColumn(headerName);
                }
                catch (SQLException se) {
                    throw new DataLoadException(String.format("%s: Database result doesn't contain field name «%s» as defined in config.", this.config.getName(), headerName));
                }
            }
            // same for the id-field
            try {
                resultSet.findColumn(Configuration.ID_FIELD_NAME);
            }
            catch (SQLException se) {
                throw new DataLoadException(String.format("%s: Database result doesn't contain field name «%s» as defined in config.", this.config.getName(), Configuration.ID_FIELD_NAME));
            }

            Document doc = null;
            while (resultSet.next()) {
                // Index record
                try{
                    doc = this.indexRecord(resultSet);
                }
                catch (Exception e) {
                    errors++;
                    String message = configName + ": Problem indexing record " + i + ", " + resultSet;
                    if (errors < config.getMaximumLoadErrors()) {
                        logger.warn(message, e);
                    }
                    else {
                        throw new DataLoadException(message, e);
                    }
                }

                // Log progress
                if (i++ % this.config.getLoadReportFrequency() == 0) {
                    logger.info("{}: Indexed {} documents", configName, i);
                    logger.debug("{}: Most recent indexed document was {}", configName, doc);
                }
            }
            recordSource.close();

            indexWriter.commit();
        }
        catch (Exception e) {
            throw new DataLoadException(configName + ": Error "+e.getMessage()+" loading records at row " + i, e);
        }
        logger.info("{}: Indexed {} records", configName, i);
    }

    private void load(File file) throws DataLoadException {
        int i = 0;
        int errors = 0;

        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.config.getAuthorityFileDelimiter().charAt(0), "\n").build();

        try (CsvMapReader mr = new CsvMapReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), customCsvPref)) {

            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertyAuthorityColumnNames()) {
                if (!headerList.contains(name)) throw new DataLoadException(String.format("%s: Header doesn't contain field name < %s > as defined in config.", this.config.getAuthorityFile().getPath(), name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new DataLoadException(String.format("%s: Id field name not found in header, should be %s!", this.config.getAuthorityFile().getPath(), idFieldName));
            Map<String, String> record;
            record = mr.read(header);

            Document doc = null;
            while (record != null) {
                // Index record
                try {
                    doc = this.indexRecord(record);
                }
                catch (Exception e) {
                    errors++;
                    String message = configName + ": Problem indexing record " + i + ", " + record;
                    if (errors < config.getMaximumLoadErrors()) {
                        logger.warn(message, e);
                    }
                    else{
                        throw new DataLoadException(message, e);
                    }
                }

                // Log process
                if (i++ % this.config.getLoadReportFrequency() == 0){
                    logger.info("{}: Indexed {} documents", configName, i);
                    logger.debug("{}: Most recent indexed document was {}", configName, doc);
                }

                // Read next record from CSV/datasource
                try {
                    record = mr.read(header);
                }
                catch (Exception e) {
                    errors++;
                    String message = configName + ": Problem reading record " + i + " «" + mr.getUntokenizedRow() + "»";
                    if (errors < config.getMaximumLoadErrors()) {
                        logger.warn(message, e);
                    }
                    else{
                        throw new DataLoadException(message, e);
                    }
                }
            }

            indexWriter.commit();
        }
        catch (Exception e) {
            throw new DataLoadException(configName + ": Error "+e.getMessage()+" loading records at line " + i, e);
        }
        logger.info("{}: Indexed {} records", configName, i);
    }

    /**
     * Adds a single record to the Lucene index
     * @param record
     * @throws Exception
     */
    private Document indexRecord(ResultSet record) throws Exception {
        Document doc = new Document();
        String idFieldName = Configuration.ID_FIELD_NAME;
        logger.trace("rawRecord: {}", record.toString());

        doc.add(new StringField(idFieldName, record.getString(idFieldName), Field.Store.YES));

        // The remainder of the columns are added as specified in the properties
        for (Property p : this.config.getProperties()) {
            String authorityName = p.getAuthorityColumnName() + Configuration.TRANSFORMED_SUFFIX;
            String value = Strings.nullToEmpty(record.getString(p.getAuthorityColumnName()));

            // Index the value in its original state, pre transformation
            Field f = new TextField(p.getAuthorityColumnName(), value, Field.Store.YES);
            doc.add(f);

            // then transform the value if necessary
            for (Transformer t:p.getAuthorityTransformers()) {
                value = t.transform(value);
            }
            // and add this one to the index
            Field fTransformed = new TextField(authorityName, value, Field.Store.YES);
            doc.add(fTransformed);

            // For some fields (those which will be passed into a fuzzy matcher like Levenshtein), we index the length
            if (p.isIndexLength()){
                Field fLength = new StringField(authorityName + Configuration.LENGTH_SUFFIX, String.format("%02d", value.length()), Field.Store.YES);
                doc.add(fLength);
            }

            if (p.isIndexInitial() & StringUtils.isNotBlank(value)){
                Field finit = new TextField(authorityName + Configuration.INITIAL_SUFFIX, value.substring(0, 1), Field.Store.YES);
                doc.add(finit);
            }
        }

        logger.trace("Document to be indexed: {}", doc.toString());
        this.indexWriter.addDocument(doc);
        return doc;
    }

    private Document indexRecord(Map<String, String> record) throws Exception {
        Document doc = new Document();
        String idFieldName = Configuration.ID_FIELD_NAME;
        logger.trace("rawRecord: {}", record.toString());
        doc.add(new StringField(idFieldName, record.get(idFieldName), Field.Store.YES));
        // The remainder of the columns are added as specified in the properties
        for (Property p : this.config.getProperties()) {
            String authorityName = p.getAuthorityColumnName() + Configuration.TRANSFORMED_SUFFIX;
            String value = record.get(p.getAuthorityColumnName());
            // super-csv treats blank as null, we don't for now
            value = (value != null) ? value: "";

            // Index the value in its original state, pre transformation..
            Field f = new TextField(p.getAuthorityColumnName(), value, Field.Store.YES);
            doc.add(f);

            // ..*then* transform the value if necessary..
            for (Transformer t:p.getAuthorityTransformers()) {
                value = t.transform(value);
            }
            //.. and add this one to the index
            Field f1 = new TextField(authorityName, value, Field.Store.YES);
            doc.add(f1);

            // For some fields (those which will be passed into a fuzzy matcher like Levenshtein), we index the length
            if (p.isIndexLength()){
                int length = 0;
                if (value != null)
                    length = value.length();
                Field fl = new StringField(authorityName + Configuration.LENGTH_SUFFIX, String.format("%02d", length), Field.Store.YES);
                doc.add(fl);
            }
            if (p.isIndexInitial() & StringUtils.isNotBlank(value)){
                Field finit = new TextField(authorityName + Configuration.INITIAL_SUFFIX, value.substring(0, 1), Field.Store.YES);
                doc.add(finit);
            }
        }
        logger.trace("Document to be indexed: {}", doc.toString());
        this.indexWriter.addDocument(doc);
        return doc;
    }

    /* • Getters and setters • */
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

	public Analyzer getLuceneAnalyzer() {
		return luceneAnalyzer;
	}
	public void setLuceneAnalyzer(Analyzer luceneAnalyzer) {
		this.luceneAnalyzer = luceneAnalyzer;
	}

    public Configuration getConfig() {
        return this.config;
    }
    @Override
    public void setConfig(Configuration config) {
        this.config = config;
		this.configName = config.getName();
    }
}
