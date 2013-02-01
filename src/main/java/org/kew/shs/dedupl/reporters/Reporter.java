package org.kew.shs.dedupl.reporters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class Reporter {

	protected static String[] ADDED_FIELDNAMES = new String[] {"cluster_size"};
	
	protected BufferedWriter bw;
	protected String delimiter;
	protected String scoreFieldName;
	protected String idFieldName;
	protected Logger log;
	
	protected Reporter (File file, String delimiter, String scoreFieldName, String idFieldName) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(file));
		this.delimiter = delimiter;
		this.scoreFieldName = scoreFieldName;
		this.idFieldName = idFieldName;
		this.log = Logger.getLogger(this.getClass());
	}
	
	protected String[] getAddedFieldnames () {
		return this.ADDED_FIELDNAMES;
	}
	
	public void report (String s) throws IOException {
		this.bw.write(s);
	}

	public void finish () throws IOException {
		this.bw.flush();
		this.bw.close();
	}

}