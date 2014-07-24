package org.kew.stringmod.dedupl.configuration;

import org.kew.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.service.resultformatter.ReconciliationResultFormatter;
import org.springframework.context.ApplicationContext;

public class ReconciliationServiceConfiguration extends MatchConfiguration {

	private ApplicationContext context;

	private Metadata reconciliationServiceMetadata;

	private QueryStringToPropertiesExtractor queryStringToPropertiesExtractor;

	private ReconciliationResultFormatter reconciliationResultFormatter;

	public ApplicationContext getContext() {
		return context;
	}
	public void setContext(ApplicationContext context) {
		this.context = context;
	}

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
