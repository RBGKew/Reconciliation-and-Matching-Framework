package org.kew.shs.dedupl.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MatchConfiguration extends Configuration {

    private File lookupFile;
    private String lookupFileEncoding = "UTF8";
    private String lookupFileDelimiter;

    private boolean outputAllMatches;
    // TODO: replace
    private String scoreFieldName;

    public String[] outputDefs() {
        List<String> sourceOutputDefs = new ArrayList<>();
        List<String> lookupOutputDefs = new ArrayList<>();
        for (Property prop:this.getProperties()) {
            if (prop.isAddOriginalSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName() + "_orig");
            if (prop.isAddTransformedSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName());
            if (prop.isAddOriginalLookupValue()) lookupOutputDefs.add("lookup_" + prop.getLookupColumnName() + "_orig");
            if (prop.isAddTransformedLookupValue()) lookupOutputDefs.add("lookup_" + prop.getLookupColumnName());
        }
        sourceOutputDefs.addAll(lookupOutputDefs);
        return sourceOutputDefs.toArray(new String[sourceOutputDefs.size()]);
    }


    // Getters and Setters
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
    public String getScoreFieldName() {
        return scoreFieldName;
    }
    public void setScoreFieldName(String scoreFieldName) {
        this.scoreFieldName = scoreFieldName;
    }
     public File getLookupFile() {
        return lookupFile;
    }
    public void setLookupFile(File lookupFile) {
        this.lookupFile = lookupFile;
    }
    public String getLookupFileEncoding() {
        return lookupFileEncoding;
    }
    public void setLookupFileEncoding(String lookupFileEncoding) {
        this.lookupFileEncoding = lookupFileEncoding;
    }
    public String getLookupFileDelimiter() {
        return lookupFileDelimiter;
    }
    public void setLookupFileDelimiter(String lookupFileDelimiter) {
        this.lookupFileDelimiter = lookupFileDelimiter;
    }
}
