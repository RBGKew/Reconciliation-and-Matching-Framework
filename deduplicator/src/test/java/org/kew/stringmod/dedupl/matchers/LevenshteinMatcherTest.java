package org.kew.stringmod.dedupl.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kew.stringmod.utils.Dictionary;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.io.Files;

public class LevenshteinMatcherTest {

    File dictFile;

    public Dictionary getDict() {
        return new Dictionary();
    }

    @Before
    public void createDictFile() throws IOException {
        File tempDir = Files.createTempDir();
        tempDir.createNewFile();
        this.dictFile = new File(tempDir, "dictFile.txt");
        CsvPreference customCsvPref = new CsvPreference.Builder('"', "&#09;".charAt(0), "\n").build();
        try (CsvListWriter writer = new CsvListWriter(new FileWriter(this.dictFile.toString()), customCsvPref)) {
            writer.write(new String[] {"hinz", "kunz"});
        }
    }

    @Test
    public void test() throws IOException {
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(2);
        assertTrue(matcher.matches("hallo", "haaallo"));
        matcher.setMaxDistance(1);
        assertFalse(matcher.matches("hallo", "haaallo"));
    }

    @Test
    public void testFalsePositives() throws IOException {
        Dictionary dict = new Dictionary();
        dict.setFilePath(this.dictFile.toString());
        dict.setFileDelimiter("&#09;");
        LevenshteinMatcher matcher = new LevenshteinMatcher();
        matcher.setMaxDistance(3);
        assertTrue(matcher.matches("hinz", "kunz"));
        matcher.setDict(dict);
        assertFalse(matcher.matches("hinz", "kunz"));
    }

}