package org.kew.shs.dedupl.configuration;

import java.io.File;

public class MatchConfiguration extends Configuration {

	private File lookupFile;
	private String lookupFileEncoding = "UTF8";
    private String lookupFileDelimiter;

	private boolean outputAllMatches;
	// TODO: replace
	private String scoreField;
	
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
	public String getScoreField() {
		return scoreField;
	}
	public void setScoreField(String scoreField) {
		this.scoreField = scoreField;
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
}
