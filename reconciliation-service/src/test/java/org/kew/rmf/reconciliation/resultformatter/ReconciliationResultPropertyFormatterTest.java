/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
