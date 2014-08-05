package org.kew.rmf.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kew.rmf.utils.Dictionary;
import org.springframework.beans.factory.InitializingBean;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * A CsvDictionary is a HashMap of Strings that is created from the first two
 * columns (key, value) of a CSV file.
 */
public class CsvDictionary implements Dictionary, InitializingBean {

	Map<String, String> map;

	private String fileDelimiter;
	private String filePath;

	public CsvDictionary() {
		map = new HashMap<>();
	}

	@Override
	public void afterPropertiesSet() throws IOException {
		CsvPreference customCsvPref = new CsvPreference.Builder('"', this.getFileDelimiter().charAt(0), "\n").build();
		try (CsvListReader reader = new CsvListReader(new FileReader(this.getFilePath()), customCsvPref)) {
			List<String> miniMap;
			while ((miniMap = reader.read()) != null) {
				map.put(miniMap.get(0), miniMap.get(1));
			}
		}
	}

	@Override
	public String get(String key) {
		return map.get(key);
	}

	@Override
	public Set<Entry<String, String>> entrySet() {
		return map.entrySet();
	}

	// • Getters and setters • //
	public String getFileDelimiter() {
		return fileDelimiter;
	}
	public void setFileDelimiter(String fileDelimiter) {
		this.fileDelimiter = fileDelimiter;
	}

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) throws IOException {
		this.filePath = filePath;
	}
}
