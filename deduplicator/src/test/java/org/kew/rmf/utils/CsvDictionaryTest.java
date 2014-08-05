package org.kew.rmf.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.Test;
import org.kew.rmf.transformers.DictionaryTransformer;
import org.kew.rmf.transformers.TransformationException;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.io.Files;

public class CsvDictionaryTest {

	@Test
	public void test() throws IOException, TransformationException {
		File tempDir = Files.createTempDir();
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
