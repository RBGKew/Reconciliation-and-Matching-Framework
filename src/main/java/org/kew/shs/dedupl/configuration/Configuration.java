package org.kew.shs.dedupl.configuration;

import java.io.File;
import java.util.List;


public class Configuration {

	private List<Property> properties;
	private File inputFile;
	private String inputFileDelimiter;
	private File outputFile;
	private String outputFileDelimiter;
	private File reportFile;
	
	private boolean writeComparisonReport=false;
	
	private int loadReportFrequency=50000;
	private int assessReportFrequency=100;

	public static String ID_FIELD_NAME ="id";

	public static final String LENGTH_SUFFIX="_length";
	public static final String ORIGINAL_SUFFIX="_orig";
	public static final String INITIAL_SUFFIX="_init";
	
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public File getInputFile() {
		return inputFile;
	}
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	public String getInputFileDelimiter() {
		return inputFileDelimiter;
	}
	public void setInputFileDelimiter(String inputFileDelimiter) {
		this.inputFileDelimiter = inputFileDelimiter;
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
	public String getOutputFileDelimiter() {
		return outputFileDelimiter;
	}
	public void setOutputFileDelimiter(String outputFileDelimiter) {
		this.outputFileDelimiter = outputFileDelimiter;
	}
	
}