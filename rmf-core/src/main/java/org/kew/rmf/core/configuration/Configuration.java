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
package org.kew.rmf.core.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kew.rmf.core.DatabaseRecordSource;
import org.kew.rmf.reporters.LuceneReporter;
import org.kew.rmf.reporters.Piper;

/**
 * A configuration holds all the information, of which the most important is
 * <ul>
 * 	<li>how the named columns are mapped to Transformers and Matchers (via {@link #properties})</li>
 * 	<li>where the data to match against ({@link #authorityFile} etc) and the data to match ({@link #queryFile} etc)</li>
 * 	<li>where and in which format to produce output (via {@link #reporters})</li>
 * </ul>
 * This information is used by the implementation of DataHandler during the process.
 */
public abstract class Configuration implements AutoCloseable {

	private String name;

	private List<Property> properties;

	private File queryFile;
	private String queryFileEncoding = "UTF-8";
	private String queryFileDelimiter;

    private String recordFilter = "";

    private int maxSearchResults = 10000;

    private boolean writeComparisonReport=false;
    private boolean writeDelimitedReport=false;
    private boolean includeNonMatchesInDelimitedReport=false;
    private int maximumLoadErrors = 0;

    private int loadReportFrequency=50000;
    private int assessReportFrequency=100;

    private List<? extends LuceneReporter> reporters;
    // a 'piper' is created for each reporter in case recordFilter is not empty
    private List<Piper> pipers = new ArrayList<>();

    private boolean reuseIndex;

    public static String ID_FIELD_NAME ="id";

    public static final String LENGTH_SUFFIX="_length";
    public static final String TRANSFORMED_SUFFIX="_transf";
    public static final String INITIAL_SUFFIX="_init";

	private File authorityFile;
	private String authorityFileEncoding = "UTF-8";
	private String authorityFileDelimiter;
	private DatabaseRecordSource authorityRecords;

	private boolean outputAllMatches;
	private String sortFieldName;

    public abstract String[] outputDefs();

    /**
     * This does two things:
     * (1) copy over some config-wide setting to each reporter
     * (2) add a Piper (a dummy reporter that does the basic job for records where no reporter
     *     needed) for each reporter
     */
    public void setupReporting() {
        for (LuceneReporter rep:this.getReporters()) {
            rep.setIdFieldName(Configuration.ID_FIELD_NAME);
            rep.setDefinedOutputFields(this.outputDefs());
            // set up a 'piper' for each reporter as a shortcut to output a record immediately (-->'continue'-ish)
            this.getPipers().add(new Piper(rep));
        }
    }

    public String[] getPropertyQueryColumnNames() {
        String[] propertyNames = new String[this.getProperties().size()];
        for (int i=0;i<propertyNames.length;i++) {
            propertyNames[i] = (this.getProperties().get(i).getQueryColumnName());
        }
        return propertyNames;
    }

    public String[] getPropertyAuthorityColumnNames() {
        String[] propertyNames = new String[this.getProperties().size()];
        for (int i=0;i<propertyNames.length;i++) {
            propertyNames[i] = (this.getProperties().get(i).getAuthorityColumnName());
        }
        return propertyNames;
    }

    @Override
    public void close() throws Exception {
        if (this.reporters != null) {
            for (LuceneReporter reporter : this.reporters) {
                reporter.close();
            }
        }
    }

    // Getters and Setters
    public boolean isOutputAllMatches() {
        return outputAllMatches;
    }
    public void setOutputAllMatches(boolean outputAllMatches) {
        this.outputAllMatches = outputAllMatches;
    }

	public String getSortFieldName() {
		return sortFieldName;
	}
	/**
	 * The field to sort results by.
	 */
	public void setSortFieldName(String sortFieldName) {
		this.sortFieldName = sortFieldName;
	}

	public File getAuthorityFile() {
		return authorityFile;
	}
	public void setAuthorityFile(File authorityFile) {
		this.authorityFile = authorityFile;
	}

	public String getAuthorityFileEncoding() {
		return authorityFileEncoding;
	}
	public void setAuthorityFileEncoding(String authorityFileEncoding) {
		this.authorityFileEncoding = authorityFileEncoding;
	}

	public String getAuthorityFileDelimiter() {
		return authorityFileDelimiter;
	}
	public void setAuthorityFileDelimiter(String authorityFileDelimiter) {
		this.authorityFileDelimiter = authorityFileDelimiter;
	}

    public List<Property> getProperties() {
        return properties;
    }
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

	public File getQueryFile() {
		return queryFile;
	}
	public String getQueryFileEncoding() {
		return queryFileEncoding;
	}

	public void setQueryFile(File queryFile) {
		this.queryFile = queryFile;
	}
	public void setQueryFileEncoding(String queryFileEncoding) {
		this.queryFileEncoding = queryFileEncoding;
	}

    public int getLoadReportFrequency() {
        return loadReportFrequency;
    }
    public void setLoadReportFrequency(int loadReportFrequency) {
        this.loadReportFrequency = loadReportFrequency;
    }

    public int getAssessReportFrequency() {
        return assessReportFrequency;
    }
    public void setAssessReportFrequency(int assessReportFrequency) {
        this.assessReportFrequency = assessReportFrequency;
    }

    public boolean isWriteComparisonReport() {
        return writeComparisonReport;
    }
    public void setWriteComparisonReport(boolean writeComparisonReport) {
        this.writeComparisonReport = writeComparisonReport;
    }

	public String getQueryFileDelimiter() {
		return queryFileDelimiter;
	}
	public void setQueryFileDelimiter(String queryFileDelimiter) {
		this.queryFileDelimiter = queryFileDelimiter;
	}

    public boolean isWriteDelimitedReport() {
        return writeDelimitedReport;
    }
    public void setWriteDelimitedReport(boolean writeDelimitedReport) {
        this.writeDelimitedReport = writeDelimitedReport;
    }

    public boolean isIncludeNonMatchesInDelimitedReport() {
        return includeNonMatchesInDelimitedReport;
    }
    public void setIncludeNonMatchesInDelimitedReport(boolean includeNonMatchesInDelimitedReport) {
        this.includeNonMatchesInDelimitedReport = includeNonMatchesInDelimitedReport;
    }

    public boolean isReuseIndex() {
        return reuseIndex;
    }
    /**
     * Whether to reuse an index, if it already exists.
     */
    public void setReuseIndex(boolean reuseIndex) {
        this.reuseIndex = reuseIndex;
    }

    public List<? extends LuceneReporter> getReporters() {
        return reporters;
    }
    public void setReporters(List<? extends LuceneReporter> reporters) {
        this.reporters = reporters;
    }

    public String getRecordFilter() {
        return recordFilter;
    }
    public void setRecordFilter(String recordFilter) {
        this.recordFilter = recordFilter;
    }

    public List<Piper> getPipers() {
        return pipers;
    }
    public void setPipers(List<Piper> pipers) {
        this.pipers = pipers;
    }

    public int getMaxSearchResults() {
        return maxSearchResults;
    }
    public void setMaxSearchResults(int maxSearchResults) {
        this.maxSearchResults = maxSearchResults;
    }

	public int getMaximumLoadErrors() {
		return maximumLoadErrors;
	}
	/**
	 * The limit of data errors before loading the configuration is aborted.
	 */
	public void setMaximumLoadErrors(int maximumLoadErrors) {
		this.maximumLoadErrors = maximumLoadErrors;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public DatabaseRecordSource getAuthorityRecords() {
		return authorityRecords;
	}
	public void setAuthorityRecords(DatabaseRecordSource authorityRecords) {
		this.authorityRecords = authorityRecords;
	}
}
