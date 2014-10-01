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
package org.kew.rmf.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

import org.junit.Test;
import org.kew.rmf.transformers.DictionaryTransformer;
import org.kew.rmf.transformers.TransformationException;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvDictionaryTest {

	@Test
	public void test() throws IOException, TransformationException {
		File tempDir = Files.createTempDirectory("CsvDictionaryTest").toFile();
		tempDir.createNewFile();
		File dictFile = new File(tempDir, "dictFile.txt");
		CsvPreference customCsvPref = new CsvPreference.Builder('"', '\t', "\n").build();
		try (CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(new FileOutputStream(dictFile.toString()), "UTF-8"), customCsvPref)) {
			writer.write(new String[] {"a", "b"});
			writer.write(new String[] {"c", "d"});
		}

		CsvDictionary dict = new CsvDictionary();
		dict.setFilePath(dictFile.toString());
		dict.setFileDelimiter("\t");
		dict.afterPropertiesSet();

		DictionaryTransformer transformer = new DictionaryTransformer();
		transformer.setDictionary(dict);

		assertEquals("b", transformer.transform("a"));
		assertEquals("b", transformer.transform("b"));
		assertEquals("d", transformer.transform("c"));
		assertEquals("d", transformer.transform("d"));
		assertEquals("e", transformer.transform("e"));
	}
}
