package org.kew.reconciliation.service.resultformatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.configuration.ReconciliationServiceConfiguration;

/**
 * Formats results according to the specified string format, with values chosen by property (result keys).
 */
public class ReconciliationResultPropertyFormatter implements ReconciliationResultFormatter {
	private String format;
	private List<String> properties;
	private String stripRepeats;

	public ReconciliationResultPropertyFormatter() {
	}

	/**
	 * Create a formatter based on the configured columns, in order, separated by commas.
	 */
	public ReconciliationResultPropertyFormatter(ReconciliationServiceConfiguration reconcilationConfig) {
		properties = new ArrayList<>();
		StringBuilder sbFormat = new StringBuilder();
		List<Property> columns = reconcilationConfig.getProperties();
		for (Property p : columns) {
			properties.add(p.getSourceColumnName());
			if (sbFormat.length() > 0) sbFormat.append(", ");
			sbFormat.append("%s");
		}
		format = sbFormat.toString();
		stripRepeats = null;
	}

	@Override
	public String formatResult(Map<String, String> result) {
		Object[] resultProperties = new Object[properties.size()];

		for (int i = 0; i < resultProperties.length; i++) {
			resultProperties[i] = result.get(properties.get(i));
		}

		if (stripRepeats != null) {
			return String.format(getFormat(), resultProperties).replace(stripRepeats+stripRepeats, stripRepeats);
		}
		else {
			return String.format(getFormat(), resultProperties);
		}
	}

	/* • Getters and setters • */
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public String getStripRepeats() {
		return stripRepeats;
	}
	public void setStripRepeats(String stripRepeats) {
		this.stripRepeats = stripRepeats;
	}
}
