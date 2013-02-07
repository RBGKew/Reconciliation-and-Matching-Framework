package org.kew.shs.dedupl.reporters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public abstract class Reporter {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size"};

	protected BufferedWriter bw;
	protected String delimiter;
	protected String scoreFieldName;
	protected String idFieldName;
	protected Logger log;
	protected boolean wantHeader = true; // TODO: make configurable

	protected Reporter (File file, String delimiter, String scoreFieldName, String idFieldName) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(file));
		this.delimiter = delimiter;
		this.scoreFieldName = scoreFieldName;
		this.idFieldName = idFieldName;
		this.log = Logger.getLogger(this.getClass());
	}

	protected String getAvailableFieldsAsString() {
		String fieldNames = "";
		for (String fieldname : this.getAvailableFields()) {
			fieldNames += (this.delimiter + fieldname);
		}
		return fieldNames;
	}
	protected String[] getAvailableFields () {
		return Reporter.AVAILABLE_FIELDS;
	}

	protected void writeHeader(String standardFields) throws IOException {
		this.bw.write(standardFields + this.getAvailableFieldsAsString() + "\n");
	}

	public void report (String s) throws IOException {
		this.bw.write(s);
	}

	public void finish () throws IOException {
		this.bw.flush();
		this.bw.close();
	}

}
