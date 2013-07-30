package org.kew.shs.dedupl.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeduplicationConfiguration extends Configuration {

    // TODO: rename, possibly move to reporters
    private File topCopyFile;

    // TODO: replace
    private String scoreFieldName;

    public String[] outputDefs() {
        List<String> sourceOutputDefs = new ArrayList<>();
        for (Property prop:this.getProperties()) {
            if (prop.isAddOriginalSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName() + "_orig");
            if (prop.isAddTransformedSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName());
        }
        return sourceOutputDefs.toArray(new String[sourceOutputDefs.size()]);
    }


    // Getters and Setters
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
