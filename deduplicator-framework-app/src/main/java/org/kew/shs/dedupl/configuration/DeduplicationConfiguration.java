package org.kew.shs.dedupl.configuration;

import java.io.File;

public class DeduplicationConfiguration extends Configuration {

	private File topCopyFile;
	private String scoreFieldName;
	
	public File getTopCopyFile() {
		return topCopyFile;
	}

	public void setTopCopyFile(File topCopyFile) {
		this.topCopyFile = topCopyFile;
	}

	public String getScoreFieldName() {
		return scoreFieldName;
	}

	public void setScoreFieldName(String scoreFieldName) {
		this.scoreFieldName = scoreFieldName;
	}
	
}
