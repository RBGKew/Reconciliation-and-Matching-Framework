package org.kew.stringmod.dedupl.lucene;

import java.io.File;
import java.io.FileReader;
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
import org.apache.lucene.store.FSDirectory;
import org.kew.stringmod.dedupl.DataLoader;
import org.kew.stringmod.dedupl.DatabaseRecordSource;
import org.kew.stringmod.dedupl.configuration.Configuration;
import org.kew.stringmod.dedupl.configuration.Property;
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

    private org.apache.lucene.util.Version luceneVersion;
    private IndexWriter indexWriter;
    private FSDirectory directory;

    private Analyzer analyzer;

    private Configuration config;

    private static Logger logger = LoggerFactory.getLogger(LuceneDataLoader.class);

    /**
     * Load the lookup data into Lucene.
     */
    @Override
    public void load() throws Exception {
        Configuration config = this.getConfig();

        /*
         * In case no file is specified we (assuming a de-duplication task) use the
         * sourceFile also for index creation.
         *
         * This is why we copy over the source-related properties to the
         * lookup-related ones
         */
        if (config.getLookupRecords() == null) {
            if (config.getLookupFile() == null) {
                for (Property p : config.getProperties()) {
                    p.setLookupTransformers(p.getSourceTransformers());
                    p.setAddOriginalLookupValue(p.isAddOriginalSourceValue());
                    p.setAddTransformedLookupValue(p.isAddTransformedSourceValue());
                    p.setLookupColumnName(p.getSourceColumnName());
                }
                config.setLookupFile(config.getSourceFile());
                config.setLookupFileEncoding(config.getSourceFileEncoding());
                config.setLookupFileDelimiter(config.getSourceFileDelimiter());
            }
            this.load(config.getLookupFile());
        }
        else {
            this.load(config.getLookupRecords());
        }
    }

    private void load(DatabaseRecordSource recordSource) throws Exception {
        int i = 0;

        ResultSet resultSet = recordSource.getResultSet();

        try (IndexWriter indexWriter = this.getIndexWriter()) {
            // check whether the necessary column names are present in the ResultSet
            for (String headerName : this.config.getPropertyLookupColumnNames()) {
                try {
                    resultSet.findColumn(headerName);
                }
                catch (SQLException se) {
                    throw new Exception(String.format("%s: Database result doesn't contain field name «%s» as defined in config.", this.config.getName(), headerName));
                }
            }
            // same for the id-field
            try {
                resultSet.findColumn(Configuration.ID_FIELD_NAME);
            }
            catch (SQLException se) {
                throw new Exception(String.format("%s: Database result doesn't contain field name «%s» as defined in config.", this.config.getName(), Configuration.ID_FIELD_NAME));
            }

            Document doc = null;
            while (resultSet.next()) {
                try{
                    doc = this.indexRecord(resultSet);
                }
                catch (Exception e) {
                    if (config.isContinueOnError()){
                        logger.warn("Problem indexing record " + i, e);
                    }
                    else{
                        throw new Exception("Problem indexing record " + i, e);
                    }
                }
                if (i++ % this.config.getLoadReportFrequency() == 0){
                    logger.info("Indexed {} documents", i);
                    logger.debug("Most recent indexed document was {}", doc);
                }
            }
            indexWriter.commit();
        }
        catch(Exception e){
            throw e;
        }
        logger.info("Indexed " + i + " documents");
    }

    private void load(File file) throws Exception {
        int i = 0;
        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.config.getLookupFileDelimiter().charAt(0), "\n").build();
        try (CsvMapReader mr = new CsvMapReader(new FileReader(file), customCsvPref);
             IndexWriter indexWriter = this.getIndexWriter()) {
            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertyLookupColumnNames()) {
                if (!headerList.contains(name)) throw new Exception(String.format("%s: Header doesn't contain field name < %s > as defined in config.", this.config.getLookupFile().getPath(), name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new Exception(String.format("%s: Id field name not found in header, should be %s!", this.config.getLookupFile().getPath(), idFieldName));
            Map<String, String> record;
            record = mr.read(header);

            Document doc = null;
            while(record != null) {
                try{
                    doc = this.indexRecord(record);
                }
                catch (Exception e) {
                	if (config.isContinueOnError()){
                		logger.warn("Problem indexing record " + i + ", " + record, e);
                	}
                	else{
                		throw new Exception("Problem indexing record " + i + ", " + record, e);
                	}
				}
                if (i++ % this.config.getLoadReportFrequency() == 0){
                    logger.info("Indexed {} documents", i);
                    logger.debug("Most recent indexed document was {}", doc);
                }
            	record = mr.read(header);
            }
            indexWriter.commit();
        }
        catch(Exception e){
        	throw e;
        }
        logger.info("Indexed " + i + " documents");
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
            String lookupName = p.getLookupColumnName() + Configuration.TRANSFORMED_SUFFIX;
            String value = Strings.nullToEmpty(record.getString(p.getLookupColumnName()));

            // Index the value in its original state, pre transformation
            Field f = new TextField(p.getLookupColumnName(), value, Field.Store.YES);
            doc.add(f);

            // then transform the value if necessary
            for (Transformer t:p.getLookupTransformers()) {
                value = t.transform(value);
            }
            // and add this one to the index
            Field fTransformed = new TextField(lookupName, value, Field.Store.YES);
            doc.add(fTransformed);

            // For some fields (those which will be passed into a fuzzy matcher like Levenshtein), we index the length
            if (p.isIndexLength()){
                Field fLength = new StringField(lookupName + Configuration.LENGTH_SUFFIX, String.format("%02d", value.length()), Field.Store.YES);
                doc.add(fLength);
            }

            if (p.isIndexInitial() & StringUtils.isNotBlank(value)){
                Field finit = new TextField(lookupName + Configuration.INITIAL_SUFFIX, value.substring(0, 1), Field.Store.YES);
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
            String lookupName = p.getLookupColumnName() + Configuration.TRANSFORMED_SUFFIX;
            String value = record.get(p.getLookupColumnName());
            // super-csv treats blank as null, we don't for now
            value = (value != null) ? value: "";

            // Index the value in its original state, pre transformation..
            Field f = new TextField(p.getLookupColumnName(), value, Field.Store.YES);
            doc.add(f);

            // ..*then* transform the value if necessary..
            for (Transformer t:p.getLookupTransformers()) {
                value = t.transform(value);
            }
            //.. and add this one to the index
            Field f1 = new TextField(lookupName, value, Field.Store.YES);
            doc.add(f1);

            // For some fields (those which will be passed into a fuzzy matcher like Levenshtein), we index the length
            if (p.isIndexLength()){
                int length = 0;
                if (value != null)
                    length = value.length();
                Field fl = new StringField(lookupName + Configuration.LENGTH_SUFFIX, String.format("%02d", length), Field.Store.YES);
                doc.add(fl);
            }
            if (p.isIndexInitial() & StringUtils.isNotBlank(value)){
                Field finit = new TextField(lookupName + Configuration.INITIAL_SUFFIX, value.substring(0, 1), Field.Store.YES);
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

    public Configuration getConfig() {
        return this.config;
    }
    @Override
    public void setConfig(Configuration config) {
        this.config = config;
    }
}
