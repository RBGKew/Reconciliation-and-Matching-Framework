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

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	@Override
	public String toString() {
		return getFilePath().substring(getFilePath().lastIndexOf('/')+1) + " (" + entrySet().size() + " entries)";
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
