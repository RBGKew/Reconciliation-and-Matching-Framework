package org.kew.shs.dedupl.configuration;

import java.io.File;

public class DeduplicationConfiguration extends Configuration {

	private File dataFile;
	private File topCopyFile;
	private boolean writeTopCopyReport=false;
	private String scoreFieldName;
	
	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	public File getTopCopyFile() {
		return topCopyFile;
	}

	public void setTopCopyFile(File topCopyFile) {
		this.topCopyFile = topCopyFile;
	}

	public boolean isWriteTopCopyReport() {
		return writeTopCopyReport;
	}
	public void setWriteTopCopyReport(boolean writeTopCopyReport) {
		this.writeTopCopyReport = writeTopCopyReport;
	}

	public String getScoreFieldName() {
		return scoreFieldName;
	}

	public void setScoreFieldName(String scoreFieldName) {
		this.scoreFieldName = scoreFieldName;
	}
	
}
