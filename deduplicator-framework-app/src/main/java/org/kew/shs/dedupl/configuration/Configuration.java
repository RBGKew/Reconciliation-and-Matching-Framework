package org.kew.shs.dedupl.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kew.shs.dedupl.reporters.LuceneReporter;
import org.kew.shs.dedupl.reporters.Piper;


/**
 * A configuration doesn't do a lot, but it holds all the information,
 * of which the most important is
 * - how the named columns are mapped to Transformers and Matchers (via `properties`)
 * - where are the input files (source and lookup file)
 * - where and in which format to produce output (via `reporters`)
 * This information is used by the implementation of DataHandler during the process.
 */
public abstract class Configuration implements AutoCloseable {

    private List<Property> properties;

    private File sourceFile;
    private String sourceFileEncoding = "UTF8";
    private String sourceFileDelimiter;

    private String recordFilter = "";

    private int maxSearchResults = 10000;

    private boolean writeComparisonReport=false;
    private boolean writeDelimitedReport=false;
    private boolean includeNonMatchesInDelimitedReport=false;

    private int loadReportFrequency=50000;
    private int assessReportFrequency=100;

    private List<? extends LuceneReporter> reporters;
    // a 'piper' is created for each reporter in case recordFilter is not empty
    private List<Piper> pipers = new ArrayList<>();

    private boolean reuseIndex;

    public static String ID_FIELD_NAME ="id";

    public static final String LENGTH_SUFFIX="_length";
    public static final String TRANSFORMED_SUFFIX="_transf";
    public static final String INITIAL_SUFFIX="_init";

    private File lookupFile;
    private String lookupFileEncoding = "UTF8";
    private String lookupFileDelimiter;

    private boolean outputAllMatches;
    // TODO: replace
    private String scoreFieldName;

    public abstract String[] outputDefs();

    public void setupReporting() {
        for (LuceneReporter rep:this.getReporters()) {
            rep.setIdFieldName(Configuration.ID_FIELD_NAME);
            rep.setDefinedOutputFields(this.outputDefs());
            // set up a 'piper' for each reporter as a shortcut to output a record immediately (-->'continue'-ish)
            this.getPipers().add(new Piper(rep));
        }
    }

    public String[] getPropertySourceColumnNames() {
        String[] propertyNames = new String[this.getProperties().size()];
        for (int i=0;i<propertyNames.length;i++) {
            propertyNames[i] = (this.getProperties().get(i).getSourceColumnName());
        }
        return propertyNames;
    }

    public String[] getPropertyLookupColumnNames() {
        String[] propertyNames = new String[this.getProperties().size()];
        for (int i=0;i<propertyNames.length;i++) {
            propertyNames[i] = (this.getProperties().get(i).getLookupColumnName());
        }
        return propertyNames;
    }

    @Override
    public void close() throws Exception {
        for (LuceneReporter reporter:this.reporters) reporter.close();
    }

    // Getters and Setters
    public File getStoreFile() {
        return lookupFile;
    }
    public void setStoreFile(File lookupFile) {
        this.lookupFile = lookupFile;
    }
    public boolean isOutputAllMatches() {
        return outputAllMatches;
    }
    public void setOutputAllMatches(boolean outputAllMatches) {
        this.outputAllMatches = outputAllMatches;
    }
    public String getScoreFieldName() {
        return scoreFieldName;
    }
    public void setScoreFieldName(String scoreFieldName) {
        this.scoreFieldName = scoreFieldName;
    }
     public File getLookupFile() {
        return lookupFile;
    }
    public void setLookupFile(File lookupFile) {
        this.lookupFile = lookupFile;
    }
    public String getLookupFileEncoding() {
        return lookupFileEncoding;
    }
    public void setLookupFileEncoding(String lookupFileEncoding) {
        this.lookupFileEncoding = lookupFileEncoding;
    }
    public String getLookupFileDelimiter() {
        return lookupFileDelimiter;
    }
    public void setLookupFileDelimiter(String lookupFileDelimiter) {
        this.lookupFileDelimiter = lookupFileDelimiter;
    }
    public List<Property> getProperties() {
        return properties;
    }
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    public File getSourceFile() {
        return sourceFile;
    }
    public String getSourceFileEncoding() {
        return sourceFileEncoding;
    }
    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
    public void setSourceFileEncoding(String sourceFileEncoding) {
        this.sourceFileEncoding = sourceFileEncoding;
    }
    public int getLoadReportFrequency() {
        return loadReportFrequency;
    }
    public void setLoadReportFrequency(int loadReportFrequency) {
        this.loadReportFrequency = loadReportFrequency;
    }
    public int getAssessReportFrequency() {
        return assessReportFrequency;
    }
    public void setAssessReportFrequency(int assessReportFrequency) {
        this.assessReportFrequency = assessReportFrequency;
    }
    public boolean isWriteComparisonReport() {
        return writeComparisonReport;
    }
    public void setWriteComparisonReport(boolean writeComparisonReport) {
        this.writeComparisonReport = writeComparisonReport;
    }
    public String getSourceFileDelimiter() {
        return sourceFileDelimiter;
    }
    public void setSourceFileDelimiter(String sourceFileDelimiter) {
        this.sourceFileDelimiter = sourceFileDelimiter;
    }
    public boolean isWriteDelimitedReport() {
        return writeDelimitedReport;
    }
    public void setWriteDelimitedReport(boolean writeDelimitedReport) {
        this.writeDelimitedReport = writeDelimitedReport;
    }
    public boolean isIncludeNonMatchesInDelimitedReport() {
        return includeNonMatchesInDelimitedReport;
    }
    public void setIncludeNonMatchesInDelimitedReport(boolean includeNonMatchesInDelimitedReport) {
        this.includeNonMatchesInDelimitedReport = includeNonMatchesInDelimitedReport;
    }
    public boolean isReuseIndex() {
        return reuseIndex;
    }
    public void setReuseIndex(boolean reuseIndex) {
        this.reuseIndex = reuseIndex;
    }
    public List<? extends LuceneReporter> getReporters() {
        return reporters;
    }
    public void setReporters(List<? extends LuceneReporter> reporters) {
        this.reporters = reporters;
    }
    public String getRecordFilter() {
        return recordFilter;
    }
    public void setRecordFilter(String recordFilter) {
        this.recordFilter = recordFilter;
    }
    public List<Piper> getPipers() {
        return pipers;
    }
    public void setPipers(List<Piper> pipers) {
        this.pipers = pipers;
    }
    public int getMaxSearchResults() {
        return maxSearchResults;
    }
    public void setMaxSearchResults(int maxSearchResults) {
        this.maxSearchResults = maxSearchResults;
    }
}
