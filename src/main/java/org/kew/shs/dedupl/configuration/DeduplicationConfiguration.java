package org.kew.shs.dedupl.configuration;

import java.io.File;

public class DeduplicationConfiguration extends Configuration {

	private File dataFile;

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}
	
}
