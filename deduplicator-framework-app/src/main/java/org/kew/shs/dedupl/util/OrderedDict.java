package org.kew.shs.dedupl.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

@SuppressWarnings("serial")
public class OrderedDict extends TreeMap<String, String> implements Dict {

    String fileDelimiter;
    String filePath;

    @Override
    public void readFile() throws IOException {
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.getFileDelimiter().charAt(0), "\n").build();
        try (CsvListReader reader = new CsvListReader(new FileReader(this.getFilePath()), customCsvPref)) {
            List<String> miniMap;
            while ((miniMap = reader.read()) != null) {
                this.put(miniMap.get(0), miniMap.get(1));
            }
        }
    }

    @Override
    public String getFileDelimiter() {
        return this.fileDelimiter;
    }

    @Override
    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void setFilePath(String filePath) throws IOException {
        this.filePath = filePath;
    }

    @Override
    public String get(String key) {
        return super.get(key);
    }

}
