package org.kew.rmf.reconciliation.resultformatter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.kew.rmf.reconciliation.service.resultformatter.ReconciliationResultPropertyFormatter;

public class ReconciliationResultPropertyFormatterTest {

	@Test
	public void testFormatResult() {
		ReconciliationResultPropertyFormatter formatter = new ReconciliationResultPropertyFormatter();
		formatter.setFormat("%s * %s : '%s'");
		formatter.setProperties(Arrays.asList(new String[]{"x", "a", "f"}));

		HashMap<String, String> result = new HashMap<>();
		result.put("x", "y");
		result.put("a", "b");
		result.put("f", "g");
		result.put("m", "n");

		String formattedResult = formatter.formatResult(result);

		assertThat(formattedResult, equalTo("y * b : 'g'"));
	}
}
