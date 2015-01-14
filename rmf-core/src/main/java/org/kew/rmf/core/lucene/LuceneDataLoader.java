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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.kew.rmf.core.DataLoader;
import org.kew.rmf.core.DatabaseRecordSource;
import org.kew.rmf.core.configuration.Configuration;
import org.kew.rmf.core.configuration.Property;
import org.kew.rmf.core.exception.DataLoadException;
import org.kew.rmf.transformers.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

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
	public void load() throws DataLoadException, InterruptedException {
		try {
			int count = -1;

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
					config.setAuthorityFileQuoteChar(config.getQueryFileQuoteChar());
				}
				// Count file
				try {
					count = this.count(config.getAuthorityFile());
				}
				catch (Exception e) {
					logger.error("Error counting records in authority file", e);
					count = -1;
				}
			}
			else {
				// Count file
				try {
					count = this.count(config.getAuthorityRecords());
				}
				catch (Exception e) {
					logger.error("Error counting records in authority database", e);
					count = -1;
				}
			}

			indexWriter = openIndex();

			// Reuse index if configured, and if it contains the expected number of records.
			if (getConfig().isReuseIndex()) {
				if (count < 0) {
					logger.info("{}: Cannot reuse index as required size is unknown", configName);
				}
				else {
					logger.debug("{}: Checking whether to reuse index, expecting {} records", configName, count);

					int numDocs = indexWriter.numDocs();
					if (numDocs == count) {
						logger.warn("{}: Reusing index, since it contains {} entries as expected", configName, numDocs);
						return;
					}
					else {
						logger.info("{}: Wiping incomplete ({}/{} records) index", configName, numDocs, count);
						indexWriter.deleteAll();
						indexWriter.commit();
					}
				}
			}

			logger.info("{}: Starting data load", configName);
			if (config.getAuthorityRecords() == null) {
				this.load(config.getAuthorityFile(), count);
			}
			else {
				this.load(config.getAuthorityRecords(), count);
			}

			int numDocs = indexWriter.numDocs();
			logger.info("{}: Completed index contains {} records", configName, numDocs);
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

		if (getConfig().isReuseIndex()) {
			// Reuse the index if it exists, otherwise create a new one.
			logger.debug("{}: Reusing existing index, if it exists", configName);
			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		}
		else {
			// Create a new index, overwriting any that already exists.
			logger.debug("{}: Overwriting existing index, if it exists", configName);
			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		}

		IndexWriter indexWriter;

		try {
			indexWriter = new IndexWriter(directory, indexWriterConfig);
		}
		catch (IOException e) {
			logger.warn("Exception while creating index, removing index directory and retrying", e);
			// Try deleting the index directory.
			File dir = directory.getDirectory();
			if (dir.isDirectory() && dir.listFiles() != null) {
				logger.warn("{}: Wiping existing index directory {}", configName, dir);
				FileUtils.deleteDirectory(dir);
			}
			indexWriter = new IndexWriter(directory, indexWriterConfig);
		}

		return indexWriter;
	}

	/**
	 * Count the number of records in a file.
	 */
	private int count(File file) throws DataLoadException, InterruptedException {
		int i = 0;
		int errors = 0;

		logger.info("{}: Counting records in {}", configName, file);

		char delimiter = this.config.getAuthorityFileDelimiter().charAt(0);
		char quote = this.config.getAuthorityFileQuoteChar().charAt(0);
		CsvPreference customCsvPref = new CsvPreference.Builder(quote, delimiter, "\n").build();

		try (CsvMapReader mr = new CsvMapReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), customCsvPref)) {
			final String[] header = mr.getHeader(true);

			Map<String, String> record;
			record = mr.read(header);

			while (record != null) {
				// Read next record from CSV/datasource
				try {
					i++;
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

			logger.info("{}: Counted {} records ({} errors)", configName, i, errors);
			return i;
		}
		catch (Exception e) {
			throw new DataLoadException(configName + ": Error "+e.getMessage()+" loading records at line " + i, e);
		}
	}

	/**
	 * Count the number of records in a file.
	 */
	private int count(DatabaseRecordSource recordSource) throws DataLoadException {
		try {
			return recordSource.count();
		}
		catch (SQLException e) {
			throw new DataLoadException(this.config.getName() + ": Unable to count records", e);
		}
	}

	private void load(DatabaseRecordSource recordSource, int total) throws DataLoadException, InterruptedException {
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
                    logger.info("{}: Indexed {} records ({}%, {} errors)", configName, i-errors, total > 0 ? ((float)i)/total*100 : "?", errors);
                    logger.debug("{}: Most recent indexed document was {}", configName, doc);
                }

                // Check for interrupt
                if (i % 10000 == 0 && Thread.interrupted()) {
                    recordSource.close();
                    indexWriter.commit();
                    logger.info("{}: Loader interrupted after indexing {}th record", configName, i);
                    throw new InterruptedException("Interrupted while loading data");
                }
            }
            recordSource.close();

            indexWriter.commit();
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DataLoadException(configName + ": Error "+e.getMessage()+" loading records at row " + i, e);
        }
        logger.info("{}: Indexed {} records", configName, i);
    }

    private void load(File file, int total) throws DataLoadException, InterruptedException {
        int i = 0;
        int errors = 0;

        char delimiter = this.config.getAuthorityFileDelimiter().charAt(0);
        char quote = this.config.getAuthorityFileQuoteChar().charAt(0);
        CsvPreference customCsvPref = new CsvPreference.Builder(quote, delimiter, "\n").build();
        logger.info("Reading CSV from {} with delimiter {} and quote {}", file, delimiter, quote);

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
            logger.debug("{}: First read record is {}", configName, record);

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
                    logger.info("{}: Indexed {} records ({}%, {} errors)", configName, i-errors, total > 0 ? ((float)i)/total*100 : "?", errors);
                    logger.debug("{}: Most recent indexed document was {}", configName, doc);
                }

                // Check for interrupt
                if (i % 10000 == 0 && Thread.interrupted()) {
                    indexWriter.commit();
                    logger.info("{}: Loader interrupted after indexing {}th record", configName, i);
                    throw new InterruptedException("Interrupted while loading data");
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
        catch (InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DataLoadException(configName + ": Error "+e.getMessage()+" loading records at line " + i, e);
        }
        logger.info("{}: Read {} records, indexed {}, {} errors", configName, i, i-errors, errors);
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
            String value = record.getString(p.getAuthorityColumnName());
            if (value == null) value = "";

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
