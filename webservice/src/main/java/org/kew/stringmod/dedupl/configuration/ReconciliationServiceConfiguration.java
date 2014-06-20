package org.kew.stringmod.dedupl.configuration;

import org.kew.reconciliation.refine.domain.metadata.Metadata;

public class ReconciliationServiceConfiguration extends MatchConfiguration {

	private Metadata reconciliationServiceMetadata;

	public Metadata getReconciliationServiceMetadata() {
		return reconciliationServiceMetadata;
	}
	public void setReconciliationServiceMetadata(Metadata reconciliationServiceMetadata) {
		this.reconciliationServiceMetadata = reconciliationServiceMetadata;
	}
}
