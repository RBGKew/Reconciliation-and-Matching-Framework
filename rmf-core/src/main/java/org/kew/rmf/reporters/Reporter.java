/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * This is a CsvReporter really.
 *
 * Its subclasses manage the output of the deduplication/matching process,
 * receive it row by row and write it wherever they are configured to do so.
 *
 * TODO: design and implement the Reporter subclass to be more generic than
 * csv-focused.
 */
public abstract class Reporter implements AutoCloseable {

    protected static String[] AVAILABLE_FIELDS = new String[] {};

    protected String name;
    protected String nameSpacePrefix = "";
    protected String configName;

    protected String delimiter;
    protected String idDelimiter;

    protected File file;
    protected CsvMapWriter writer;

    protected String sortFieldName;
    protected String idFieldName;
    protected Logger logger;
    protected boolean wantHeader = true; // TODO: make configurable

    protected boolean isStart = true;
    protected String[] definedOutputFields;
    protected String[] header;

    protected Reporter () {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    protected String[] getAvailableFields () {
        return Reporter.AVAILABLE_FIELDS;
    }

    protected String getAvailableFieldsAsString() {
        return StringUtils.join(this.getAvailableFields(), ", ");
    }

    protected void writeHeader() throws IOException {
        String[] fields = this.getAvailableFields();
        // name-space the field names
        for (int i=0;i<fields.length;i++) {
            fields[i] = this.getNameSpacePrefix() + fields[i];
        }
        this.header = (String[]) ArrayUtils.addAll(new String[] {"id"}, ArrayUtils.addAll(this.definedOutputFields, fields));
        this.writer.writeHeader(header);
    }

    public abstract void reportResults (Map<String, String> record, List<Map<String, String>> results) throws Exception;

    /**
     * Meta method to deal with a 'main' record and a list of attached maps, the
     * results of a deduplication/matching process.
     *
     * 'Meta' means it only writes a header if wanted and no header is written yet,
     * and then passes the objects back to the method reportResults by the calling subclass
     * If there is any meaning in the order of the results, it will be preserved
     * but is expected to be already sorted.
     *
     * @param record
     * @param results
     * @throws IOException
     */
    public void report (Map<String, String> record, List<Map<String, String>> results) throws Exception {
        if (this.isStart) {
            this.setWriter();
            if (this.wantHeader) this.writeHeader();
            this.setStart(false);
        }
        logger.debug("Reporting record {} with results {}", record, results);
        this.reportResults(record, results);
    }

    protected static Map<String, String> getNamespacedCopy(Map<String, String> record, String namespace) {
        Map<String, String> copy = new HashMap<>();
        for (String key:record.keySet()) copy.put(namespace + key, record.get(key));
        return copy;
    }

    @Override
    public void close() throws IOException {
        this.writer.flush();
        this.writer.close();
    }

    protected String getClusterSize(Map<String, String> fromRecord, List<Map<String, String>> cluster){
        return Integer.toString(cluster.size());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile(File file) throws IOException {
        this.file = file;
    }


    public void setWriter() throws IOException {
        // TODO: either make quote characters and line break characters configurable or simplify even more?
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.getDelimiter().charAt(0), "\n").build();
        this.writer = new CsvMapWriter(new OutputStreamWriter(new FileOutputStream(this.file), "UTF-8"), customCsvPref);
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

    public String getSortFieldName() {
        return sortFieldName;
    }

    public void setSortFieldName(String sortFieldName) {
        this.sortFieldName = sortFieldName;
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

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public String[] getDefinedOutputFields() {
        return definedOutputFields;
    }

    public void setDefinedOutputFields(String[] definedOutputFields) {
        this.definedOutputFields = definedOutputFields;
    }

    public String getNameSpacePrefix() {
        return nameSpacePrefix;
    }

    public void setNameSpacePrefix(String nameSpacePrefix) {
        this.nameSpacePrefix = nameSpacePrefix;
    }

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

}
