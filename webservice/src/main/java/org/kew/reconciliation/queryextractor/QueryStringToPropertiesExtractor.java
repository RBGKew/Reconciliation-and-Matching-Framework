package org.kew.reconciliation.queryextractor;

import org.kew.reconciliation.refine.domain.query.Property;

public interface QueryStringToPropertiesExtractor {
	public Property[] extractProperties(String queryString);
}
