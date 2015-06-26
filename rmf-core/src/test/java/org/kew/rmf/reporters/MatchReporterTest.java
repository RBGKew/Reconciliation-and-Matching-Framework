/*
 * Reconciliation and Matching Framework
 * Copyright © 2015 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reporters;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class MatchReporterTest {
	private static final Logger logger = LoggerFactory.getLogger(MatchReporterTest.class);

	@Test
	public void testQuotedCsv() throws Exception {
		File tempFile = File.createTempFile("MatchReporterTest-", ".tsv");

		MatchReporter reporter = new MatchReporter();

		reporter.setName("MatchReporter");
		reporter.setConfigName("Test_Config");
		reporter.setDelimiter("\t");
		reporter.setIdDelimiter("|");
		reporter.setFile(tempFile);

		reporter.setWriter();

		reporter.setDefinedOutputFields(new String[]{"property", "quote-test"});

		reporter.writeHeader();

		Map<String,String> fromRecord = new HashMap<>();
		List<Map<String,String>> matches = new ArrayList<>();

		String[][] records = new String[][]{
				new String[]{"test-id", "test-value", "ABC 'DEF'  GHI"},
				new String[]{"id2", "value2", "ABC \"DEF\" GHI"},
				new String[]{"id3", "value3", "ABC \"\"DEF\"\" GHI"},
				new String[]{"id4", "value4", "\"ABC DEF GHI\""},
				new String[]{"id2", "value2", "\"ABC DEF GHI"},
				new String[]{"id2", "value2", "\"\"ABC DEF GHI"},
				new String[]{"id2", "value2", "\""},
				new String[]{"id2", "value2", "\"\""},
				new String[]{"id2", "value2", "\"\"\""}
		};

		for (String[] r : records) {
			fromRecord.put("id", r[0]);
			fromRecord.put("property", r[1]);
			fromRecord.put("quote-test", r[2]);

			reporter.reportResults(fromRecord, matches);
		}

		reporter.close();

		// Read in file and check it's the same
		CsvPreference customCsvPref = new CsvPreference.Builder('"', '\t', "\n").build();

		int i = 0;
		try (CsvMapReader mr = new CsvMapReader(new InputStreamReader(new FileInputStream(tempFile), "UTF-8"), customCsvPref)) {
			final String[] header = mr.getHeader(true);

			logger.info("CSV header is {}", (Object[])header);

			Map<String, String> record;
			record = mr.read(header);

			while (record != null) {
				logger.info("Expecting record {}, {}, {}", records[i][0], records[i][1], records[i][2]);
				logger.info("Read record {}", record);

				assertTrue(record.get("id").equals(records[i][0]));
				assertTrue(record.get("property").equals(records[i][1]));
				assertTrue(record.get("quote-test").equals(records[i][2]));

				// Read next record from CSV/datasource
				record = mr.read(header);
				i++;
			}
		}
		catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
}
