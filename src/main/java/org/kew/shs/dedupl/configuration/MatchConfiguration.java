package org.kew.shs.dedupl.configuration;

import java.io.File;

public class MatchConfiguration extends Configuration {

	private File storeFile;
	private File iterateFile;

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
	
}
