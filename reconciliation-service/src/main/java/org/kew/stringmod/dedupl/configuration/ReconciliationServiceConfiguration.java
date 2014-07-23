package org.kew.stringmod.dedupl.configuration;

import org.kew.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.reconciliation.queryextractor.ScientificNameToPropertiesConverter;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.service.resultformatter.ReconciliationResultFormatter;

public class ReconciliationServiceConfiguration extends MatchConfiguration {

	private Metadata reconciliationServiceMetadata;

	private QueryStringToPropertiesExtractor queryStringToPropertiesExtractor = new ScientificNameToPropertiesConverter();

	private ReconciliationResultFormatter reconciliationResultFormatter;

	public Metadata getReconciliationServiceMetadata() {
		return reconciliationServiceMetadata;
	}
	public void setReconciliationServiceMetadata(Metadata reconciliationServiceMetadata) {
		this.reconciliationServiceMetadata = reconciliationServiceMetadata;
	}

	public QueryStringToPropertiesExtractor getQueryStringToPropertiesExtractor() {
		return queryStringToPropertiesExtractor;
	}
	public void setQueryStringToPropertiesExtractor(QueryStringToPropertiesExtractor queryStringToPropertiesExtractor) {
		this.queryStringToPropertiesExtractor = queryStringToPropertiesExtractor;
	}

	public ReconciliationResultFormatter getReconciliationResultFormatter() {
		return reconciliationResultFormatter;
	}
	public void setReconciliationResultFormatter(ReconciliationResultFormatter reconciliationResultFormatter) {
		this.reconciliationResultFormatter = reconciliationResultFormatter;
	}
}
