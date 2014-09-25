package org.kew.rmf.reconciliation.queryextractor;

import org.kew.rmf.refine.domain.query.Property;

public interface QueryStringToPropertiesExtractor {
	public Property[] extractProperties(String queryString);
}
