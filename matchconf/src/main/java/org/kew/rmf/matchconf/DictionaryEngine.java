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
package org.kew.rmf.matchconf;

import java.util.ArrayList;

/**
 * Writes out the xml to configure the 'real' deduplication/matching process of this specific
 * {@link Dictionary} instance.
 */
public class DictionaryEngine {
	
	Dictionary dict;
	
	public DictionaryEngine(Dictionary dict) {
		this.dict = dict;
	}
	
	public ArrayList<String> toXML(int indentLevel) throws Exception {
		int shiftWidth = 4;
		String shift = String.format("%" + shiftWidth + "s", " ");
		String indent = "";
		for (int i=0;i<indentLevel;i++) {
			indent += shift;
		}
		ArrayList<String> outXML = new ArrayList<String>();
		outXML.add(String.format("%s<bean id=\"%s\" class=\"org.kew.rmf.utils.CsvDictionary\"", indent, this.dict.getName()));
        outXML.add(String.format("%s%sp:fileDelimiter=\"%s\"", indent, shift, this.dict.getFileDelimiter()));
        // change path to unix-style for convencience, even if on windows..
        outXML.add(String.format("%s%sp:filePath=\"%s\" />", indent, shift, this.dict.getFilePath().replace("\\\\", "/")));
		return outXML;
	}
}
