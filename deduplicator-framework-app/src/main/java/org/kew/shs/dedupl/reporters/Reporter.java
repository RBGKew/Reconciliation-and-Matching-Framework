package org.kew.shs.dedupl.reporters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public abstract class Reporter implements AutoCloseable {

	protected static String[] AVAILABLE_FIELDS = new String[] {"cluster_size"};
	
	protected String name;

	protected String delimiter;
	protected String idDelimiter;

	protected BufferedWriter bw;
	protected String scoreFieldName;
	protected String idFieldName;
	protected Logger log;
	protected boolean wantHeader = true; // TODO: make configurable

	protected Reporter () {
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

	public void close() throws IOException {
		this.bw.flush();
		this.bw.close();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFile(File file) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(file));
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getIdDelimiter() {
		return idDelimiter;
	}

	public void setIdDelimiter(String idDelimiter) {
		this.idDelimiter = idDelimiter;
	}

	public String getScoreFieldName() {
		return scoreFieldName;
	}

	public void setScoreFieldName(String scoreFieldName) {
		this.scoreFieldName = scoreFieldName;
	}

	public String getIdFieldName() {
		return idFieldName;
	}

	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	public boolean isWantHeader() {
		return wantHeader;
	}

	public void setWantHeader(boolean wantHeader) {
		this.wantHeader = wantHeader;
	}

}
