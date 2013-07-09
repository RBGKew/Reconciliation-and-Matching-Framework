package org.kew.shs.dedupl.lucene;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * This is a Lucene implementation of the DataLoader interface
 * @author nn00kg
 *
 */
public class LuceneDataLoader implements DataLoader {

    private org.apache.lucene.util.Version luceneVersion;
    private IndexWriter indexWriter;
    private FSDirectory directory;

    private Analyzer analyzer;

    private Configuration config;

    private static Logger log = Logger.getLogger(LuceneDataLoader.class);

    public void load() throws Exception {
        load(config.getInputFile());
    }

    public void load(File file) throws Exception {
        int i = 0;
        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.config.getInputFileDelimiter().charAt(0), "\n").build();
        try (CsvMapReader mr = new CsvMapReader(new FileReader(file), customCsvPref)) {
            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertyNames()) {
                if (!headerList.contains(name)) throw new Exception(String.format("Header doesn't contain field name < %s > as defined in config.", name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new Exception(String.format("Id field name not found in header, should be %s!", idFieldName));
            Map<String, String> record;
            while((record = mr.read(header)) != null) {
                Document doc = new Document();
                log.debug(record.toString());
                doc.add(new Field(Configuration.ID_FIELD_NAME, record.get(idFieldName), Field.Store.YES,Field.Index.ANALYZED));
                // The remainder of the columns are added as specified in the properties
                for (Property p : this.config.getProperties()) {
                    String value = record.get(p.getName());
                    // super-csv treats blank as null, we don't for now
                    value = (value != null) ? value: "";

                    if (p.isIndexOriginal()){
                        // Index value in its original state, pre transformation
                        Field f1 = new Field(p.getName() + Configuration.ORIGINAL_SUFFIX, value, Field.Store.YES,Field.Index.ANALYZED);
                        doc.add(f1);
                    }

                    // Transform the value if necessary
                    if (p.getTransformer() !=null)
                        value = p.getTransformer().transform(value);
                    Field f = new Field(p.getName(), value, Field.Store.YES,Field.Index.ANALYZED);
                    doc.add(f);

                    // For some fields (those which will be passed into a fuzzy matcher like Levenshtein), we index the length
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
                if (i++ % this.config.getLoadReportFrequency() == 0)
                    log.info("Indexed " + i + " documents");
            }
            indexWriter.commit();
        }
        log.info("Indexed " + i + " documents");
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
        return this.config;
    }

    public void setConfiguration(Configuration config) {
        this.config = config;
    }

}
