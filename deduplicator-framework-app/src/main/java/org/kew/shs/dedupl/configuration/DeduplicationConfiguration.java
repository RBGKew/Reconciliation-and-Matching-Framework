package org.kew.shs.dedupl.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The important aspect of a DeduplicationConfiguration is that for a
 * deduplication process there is no real differentiation between source and
 * lookup file as we only have one file that is matched to itself.
 *
 */
public class DeduplicationConfiguration extends Configuration {

    // TODO: rename, possibly move to reporters
    private File topCopyFile;

    // TODO: replace
    private String scoreFieldName;

    @Override
    public String[] outputDefs() {
        List<String> sourceOutputDefs = new ArrayList<>();
        for (Property prop:this.getProperties()) {
            if (prop.isAddOriginalSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName());
            if (prop.isAddTransformedSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName() + "_transf");
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
