package org.kew.stringmod.dedupl.configuration;

import java.util.ArrayList;
import java.util.List;

public class MatchConfiguration extends Configuration {

	@Override
	public String[] outputDefs() {
		List<String> queryOutputDefs = new ArrayList<>();
		List<String> authorityOutputDefs = new ArrayList<>();
		for (Property prop : this.getProperties()) {
			if (prop.isAddOriginalQueryValue()) queryOutputDefs.add(prop.getQueryColumnName());
			if (prop.isAddTransformedQueryValue()) queryOutputDefs.add(prop.getQueryColumnName() + "_transf");
			if (prop.isAddOriginalAuthorityValue()) authorityOutputDefs.add("authority_" + prop.getAuthorityColumnName());
			if (prop.isAddTransformedAuthorityValue()) authorityOutputDefs.add("authority_" + prop.getAuthorityColumnName() + "_transf");
		}
		queryOutputDefs.addAll(authorityOutputDefs);
		return queryOutputDefs.toArray(new String[queryOutputDefs.size()]);
	}
}
