package org.kew.stringmod.dedupl.configuration;

import java.util.ArrayList;
import java.util.List;



public class MatchConfiguration extends Configuration {

    @Override
    public String[] outputDefs() {
        List<String> sourceOutputDefs = new ArrayList<>();
        List<String> lookupOutputDefs = new ArrayList<>();
        for (Property prop:this.getProperties()) {
            if (prop.isAddOriginalSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName());
            if (prop.isAddTransformedSourceValue()) sourceOutputDefs.add(prop.getSourceColumnName() + "_transf");
            if (prop.isAddOriginalLookupValue()) lookupOutputDefs.add("lookup_" + prop.getLookupColumnName());
            if (prop.isAddTransformedLookupValue()) lookupOutputDefs.add("lookup_" + prop.getLookupColumnName() + "_transf");
        }
        sourceOutputDefs.addAll(lookupOutputDefs);
        return sourceOutputDefs.toArray(new String[sourceOutputDefs.size()]);
    }

}
