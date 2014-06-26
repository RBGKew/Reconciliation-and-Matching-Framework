package org.kew.stringmod.dedupl.lucene;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.kew.stringmod.dedupl.DataHandler;
import org.kew.stringmod.dedupl.configuration.Configuration;
import org.kew.stringmod.dedupl.configuration.MatchConfiguration;
import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.exception.DataLoadException;
import org.kew.stringmod.dedupl.exception.MatchExecutionException;
import org.kew.stringmod.dedupl.exception.TooManyMatchesException;
import org.kew.stringmod.dedupl.matchers.MatchException;
import org.kew.stringmod.dedupl.reporters.LuceneReporter;
import org.kew.stringmod.dedupl.reporters.Piper;
import org.kew.stringmod.lib.transformers.TransformationException;
import org.kew.stringmod.lib.transformers.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Performs the actual match against the Lucene index.
 *
 * {@link #getMatches(Map, int)} returns a list of matches.
 */
public class LuceneMatcher extends LuceneHandler<MatchConfiguration> implements DataHandler<MatchConfiguration> {
	private static final Logger logger = LoggerFactory.getLogger(LuceneMatcher.class);

    protected MatchConfiguration matchConfig;

	@Override // from DataHandler
	public void loadData() throws DataLoadException {
		this.dataLoader.setConfig(this.getConfig());
		this.dataLoader.load();
	}

    /**
     * Performs a match against the Lucene index, and returns a list of matches.
     * @param record The record needing to be matched
     * @param maximumMatches Maximum number of matches to return, otherwise throws TooManyMatchesException
     * @return A list of matched records
     * @throws TooManyMatchesException Thrown if more than maximumMatches matches are found
     * @throws MatchExecutionException For other errors finding a match
     */
    public List<Map<String,String>> getMatches(Map<String, String> record, int maximumMatches) throws TooManyMatchesException, MatchExecutionException {
        // pipe everything through to the output where an existing filter evaluates to false;
        try {
            if (!StringUtils.isBlank(config.getRecordFilter()) && !jsEnv.evalFilter(config.getRecordFilter(), record)) {
                logger.debug("All records excluded by record filter");
                return new ArrayList<>();
            }
        }
        catch (ScriptException e) {
            throw new MatchExecutionException("Error evaluating recordFilter on record "+record, e);
        }
        // transform fields where required
        for (Property prop:config.getProperties()) {
            String fName = prop.getSourceColumnName();
            String fValue = record.get(fName);
            // transform the field-value..
            fValue = fValue == null ? "" : fValue; // super-csv treats blank as null, we don't for now
            for (Transformer t:prop.getSourceTransformers()) {
                try {
                    fValue = t.transform(fValue);
                }
                catch (TransformationException e) {
                    throw new MatchExecutionException("Error evaluating transformer "+t+" on record "+record, e);
                }
            }
            // ..and put it into the record
            record.put(fName + Configuration.TRANSFORMED_SUFFIX, fValue);
        }

        String fromId = record.get(Configuration.ID_FIELD_NAME);
        // Use the properties to select a set of documents which may contain matches
        String querystr = LuceneUtils.buildQuery(config.getProperties(), record, false);
        // If the query for some reasons results being empty we pipe the record directly through to the output
        // TODO: create a log-file that stores critical log messages?
        if (querystr.equals("")) {
            logger.warn("Empty query for record {}", record);
            return new ArrayList<>();
        }

        // Perform the match
        TopDocs td;
        try {
            td = queryLucene(querystr, this.getIndexSearcher(), config.getMaxSearchResults());
            if (td.totalHits >= config.getMaxSearchResults()) {
                logger.info("Error matching {}", "query");
                throw new TooManyMatchesException(String.format("Number of max search results exceeded for record %s! You should either tweak your config to bring back less possible results making better use of the \"useInSelect\" switch (recommended) or raise the \"maxSearchResults\" number.", record));
            }
            logger.debug("Found {} possibles to assess against {}", td.totalHits, fromId);
        }
        catch (ParseException | IOException e) {
            throw new MatchExecutionException("Error querying Lucene on query "+record, e);
        }

        List<Map<String, String>> matches = new ArrayList<>();

        for (ScoreDoc sd : td.scoreDocs){
            try {
                Document toDoc = getFromLucene(sd.doc);
                if (LuceneUtils.recordsMatch(record, toDoc, config.getProperties())) {
                    maximumMatches++;
                    Map<String,String> toDocAsMap = LuceneUtils.doc2Map(toDoc);
                    matches.add(toDocAsMap);
                    logger.info("Match is {}", toDocAsMap);
                }
            }
            catch (MatchException e) {
                throw new MatchExecutionException("Error running matcher for "+record, e);
            }
            catch (IOException e) {
                throw new MatchExecutionException("Error retrieving match result from Lucene "+sd.doc, e);
            }
        }
        sortMatches(matches);
        return matches;
    }

    public void sortMatches(List<Map<String, String>> matches) {
        final String sortOn = config.getScoreFieldName();
        try {
            Collections.sort(matches, Collections.reverseOrder(new Comparator<Map<String, String>>() {
                @Override
                public int compare(final Map<String, String> m1,final Map<String, String> m2) {
                    return Integer.valueOf(m1.get(sortOn)).compareTo(Integer.valueOf(m2.get(sortOn)));
                }
            }));
        } catch (NumberFormatException e) {
            // if the String can't be converted to an integer we do String comparison
            Collections.sort(matches, Collections.reverseOrder(new Comparator<Map<String, String>>() {
                @Override
                public int compare(final Map<String, String> m1,final Map<String, String> m2) {
                    return m1.get(sortOn).compareTo(m2.get(sortOn));
                }
            }));
        }
    }

    /**
     * Run the whole matching task.
     *
     * The iterative flow is:
     * - load the data (== write the lucene index)
     * - iterate over the source data file
     *     - for each record, look for matches in the index
     *    - for each record, report into new fields of this record about matches via reporters
     *
     * The main difference to a deduplication task as defined by {@link LuceneDeduplicator}
     * is that we use two different datasets, one to create the lookup index, the other one as
     * source file (where we iterate over each record to look up possible matches).
     */
    @Override
    public void run() throws Exception {

        this.loadData(); // writes the index according to the configuration

        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference csvPref = new CsvPreference.Builder(
                '"', this.getConfig().getSourceFileDelimiter().charAt(0), "\n").build();
        int i = 0;
        try (MatchConfiguration config = this.getConfig();
             IndexReader indexReader= this.getIndexReader();
             IndexWriter indexWriter = this.indexWriter;
             CsvMapReader mr = new CsvMapReader(new FileReader(this.getConfig().getSourceFile()), csvPref)) {

            this.prepareEnvs();

            // loop over the sourceFile
            int numMatches = 0;
            final String[] header = mr.getHeader(true);
            // check whether the header column names fit to the ones specified in the configuration
            List<String> headerList = Arrays.asList(header);
            for (String name:this.config.getPropertySourceColumnNames()) {
                if (!headerList.contains(name)) throw new Exception(String.format("%s: Header doesn't contain field name < %s > as defined in config.", this.config.getSourceFile().getPath(), name));
            }
            // same for the id-field
            String idFieldName = Configuration.ID_FIELD_NAME;
            if (!headerList.contains(idFieldName)) throw new Exception(String.format("%s: Id field name not found in header, should be %s!", this.config.getSourceFile().getPath(), idFieldName));
            Map<String, String> record;
            while((record = mr.read(header)) != null) {
                List<Map<String, String>> matches = getMatches(record, numMatches);
                if (matches == null) {
                    for (Piper piper:config.getPipers()) piper.pipe(record);
                    continue;
                }
                if (i++ % config.getAssessReportFrequency() == 0) logger.info("Assessed " + i + " records, found " + numMatches + " matches");
                // call each reporter that has a say; all they get is a complete list of duplicates for this record.
                for (LuceneReporter reporter : config.getReporters()) {
                    // TODO: make idFieldName configurable, but not on reporter level
                    reporter.report(record, matches);
                }
            }
            logger.info("Assessed " + i + " records, found " + numMatches + " matches");
        }
    }
}
