package org.kew.shs.dedupl.configuration;

import java.io.File;

public class MatchConfiguration extends Configuration {

	private File storeFile;
	private File iterateFile;
	private boolean outputAllMatches;
	private String scoreField;
	private boolean reuseIndex;
	
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
	public boolean isReuseIndex() {
		return reuseIndex;
	}
	public void setReuseIndex(boolean reuseIndex) {
		this.reuseIndex = reuseIndex;
	}
}
