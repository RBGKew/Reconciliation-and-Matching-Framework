package org.kew.rmf.utils;

import java.util.LinkedHashMap;

/**
 * Same as a {@link CsvDictionary}, but maintains order of insertion.
 */
public class CsvOrderedDictionary extends CsvDictionary {
	public CsvOrderedDictionary() {
		map = new LinkedHashMap<>();
	}
}
