package org.kew.shs.dedupl.configuration;

import java.io.File;

public class MatchConfiguration extends Configuration {

	private File storeFile;
	private File iterateFile;
	public boolean outputAllMatches;
	public String scoreField;
	
	public File getStoreFile() {
		return storeFile;
	}
	public void setStoreFile(File storeFile) {
		this.storeFile = storeFile;
	}
	public File getIterateFile() {
		return iterateFile;
	}
	public void setIterateFile(File iterateFile) {
		this.iterateFile = iterateFile;
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
	 
}
