package org.kew.shs.dedupl.reporters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class MatchReporter extends LuceneReporter {

    protected static String[] AVAILABLE_FIELDS = new String[] {"no_matches", "matching_ids"};

    public MatchReporter() {
        logger.info("I will be creating an enhanced output file for you with additional fields: " +
                this.getAvailableFieldsAsString());
    }

    protected String[] getAvailableFields () {
        return (String[]) ArrayUtils.addAll(super.getAvailableFields(), MatchReporter.AVAILABLE_FIELDS);
    }

    /*
     * Returns one line for one identified cluster containing the stored fields for
     * the 'best' record + additional info
     *
     */
    @Override
    public void reportResults (Map<String, String> fromRecord, List<Map<String, String>> matches) throws IOException {
        fromRecord.put("no_matches", this.getClusterSize(fromRecord, matches));
        fromRecord.put("matching_ids", this.getIDsInCluster(fromRecord, matches, this.getIdDelimiter()));
        // add the lookup values of the best match
        if (matches.size() > 0) fromRecord.putAll(Reporter.getNamespacedCopy(matches.get(0), "lookup_"));
        this.writer.write(fromRecord, this.header);
    }

}
