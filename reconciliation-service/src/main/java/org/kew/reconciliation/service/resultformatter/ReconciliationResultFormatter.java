package org.kew.reconciliation.service.resultformatter;

import java.util.Map;

/**
 * Defines how a result should be formatted in the JSON response to a reconciliation query.
 */
public interface ReconciliationResultFormatter {
	public String formatResult(Map<String, String> result);
}
