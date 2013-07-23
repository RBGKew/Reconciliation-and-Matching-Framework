package org.kew.shs.dedupl.configuration;

import java.io.File;

public class MatchConfiguration extends Configuration {

	private File lookupFile;
	private boolean outputAllMatches;
	private String scoreField;
	
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

}
