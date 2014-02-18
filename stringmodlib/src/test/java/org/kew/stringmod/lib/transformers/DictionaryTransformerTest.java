package org.kew.stringmod.lib.transformers;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kew.stringmod.lib.transformers.DictionaryTransformer;
import org.kew.stringmod.utils.Dict;
import org.kew.stringmod.utils.Dictionary;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.io.Files;


public class DictionaryTransformerTest {

    File dictFile;

	public Dict getDict() {
		return new Dictionary();
	}
	
    @Before
    public void createDictFile() throws IOException {
        File tempDir = Files.createTempDir();
        tempDir.createNewFile();
        this.dictFile = new File(tempDir, "dictFile.txt");
        CsvPreference customCsvPref = new CsvPreference.Builder('"', "&#09;".charAt(0), "\n").build();
        try (CsvListWriter writer = new CsvListWriter(new FileWriter(this.dictFile.toString()), customCsvPref)) {
            writer.write(new String[] {"a", "b"});
            writer.write(new String[] {"c", "d"});
        }
    }

    @Test
    public void test() throws IOException {
        Dict dict = new Dictionary();
        dict.setFilePath(this.dictFile.toString());
        dict.setFileDelimiter("&#09;");
        DictionaryTransformer transformer = new DictionaryTransformer();
        transformer.setDict(dict);
        assertThat(transformer.transform("a"), equalTo("b"));
        assertThat(transformer.transform("b"), equalTo("b"));
        assertThat(transformer.transform("c"), equalTo("d"));
        assertThat(transformer.transform("d"), equalTo("d"));
        assertThat(transformer.transform("e"), equalTo("e"));
    }

}
