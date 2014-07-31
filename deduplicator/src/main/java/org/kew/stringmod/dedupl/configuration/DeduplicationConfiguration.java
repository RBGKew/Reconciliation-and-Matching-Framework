package org.kew.stringmod.dedupl.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * The important aspect of a DeduplicationConfiguration is that for a
 * deduplication process there is no real differentiation between query and
 * authority as we only have one file that is matched to itself.
 */
public class DeduplicationConfiguration extends Configuration {

	@Override
	public String[] outputDefs() {
		List<String> queryOutputDefs = new ArrayList<>();
		for (Property prop : this.getProperties()) {
			if (prop.isAddOriginalQueryValue()) queryOutputDefs.add(prop.getQueryColumnName());
			if (prop.isAddTransformedQueryValue()) queryOutputDefs.add(prop.getQueryColumnName() + "_transf");
		}
		return queryOutputDefs.toArray(new String[queryOutputDefs.size()]);
	}
}
