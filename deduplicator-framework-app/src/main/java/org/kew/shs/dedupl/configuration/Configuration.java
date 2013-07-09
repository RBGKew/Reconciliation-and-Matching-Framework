package org.kew.shs.dedupl.configuration;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Configuration {

    private List<Property> properties;

    private File inputFile;
    private String inputFileEncoding = "UTF8";
    private String inputFileDelimiter;

    private File outputFile;
    private String outputFileDelimiter;
    private String outputFileIdDelimiter;

    private File reportFile;
    private File delimitedFile;

    private boolean writeComparisonReport=false;
    private boolean writeDelimitedReport=false;
    private boolean includeNonMatchesInDelimitedReport=false;

    private int loadReportFrequency=50000;
    private int assessReportFrequency=100;

    public static String ID_FIELD_NAME ="id";

    public static final String LENGTH_SUFFIX="_length";
    public static final String ORIGINAL_SUFFIX="_orig";
    public static final String INITIAL_SUFFIX="_init";

    public String[] getPropertyNames() {
        String[] propertyNames = new String[this.getProperties().size()];
        for (int i=0;i<propertyNames.length;i++) {
            propertyNames[i] = (this.getProperties().get(i).getName());
        }
        return propertyNames;
    }
    public List<Property> getProperties() {
        return properties;
    }
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    public File getInputFile() {
        return inputFile;
    }
    public String getInputFileEncoding() {
        return inputFileEncoding;
    }
    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }
    public void setInputFileEncoding(String inputFileEncoding) {
        this.inputFileEncoding = inputFileEncoding;
    }
    public File getOutputFile() {
        return outputFile;
    }
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
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
    public File getReportFile() {
        return reportFile;
    }
    public void setReportFile(File reportFile) {
        this.reportFile = reportFile;
    }
    public String getInputFileDelimiter() {
        return inputFileDelimiter;
    }
    public void setInputFileDelimiter(String inputFileDelimiter) {
        this.inputFileDelimiter = inputFileDelimiter;
    }
    public String getOutputFileDelimiter() {
        return outputFileDelimiter;
    }
    public void setOutputFileDelimiter(String outputFileDelimiter) {
        this.outputFileDelimiter = outputFileDelimiter;
    }
    public String getOutputFileIdDelimiter() {
        return outputFileIdDelimiter;
    }
    public void setOutputFileIdDelimiter(String outputFileIdDelimiter) {
        this.outputFileIdDelimiter = outputFileIdDelimiter;
    }

    public boolean isWriteDelimitedReport() {
        return writeDelimitedReport;
    }
    public void setWriteDelimitedReport(boolean writeDelimitedReport) {
        this.writeDelimitedReport = writeDelimitedReport;
    }
    public File getDelimitedFile() {
        return delimitedFile;
    }
    public void setDelimitedFile(File delimitedFile) {
        this.delimitedFile = delimitedFile;
    }

    public boolean isIncludeNonMatchesInDelimitedReport() {
        return includeNonMatchesInDelimitedReport;
    }
    public void setIncludeNonMatchesInDelimitedReport(boolean includeNonMatchesInDelimitedReport) {
        this.includeNonMatchesInDelimitedReport = includeNonMatchesInDelimitedReport;
    }

}
