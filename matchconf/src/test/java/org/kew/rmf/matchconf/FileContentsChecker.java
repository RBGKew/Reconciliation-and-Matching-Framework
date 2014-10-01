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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FileContentsChecker {

	public static void checkFilesSame(File actualFile, String expectedContents, String replacementText) throws IOException {
		assertThat("File exists", actualFile.exists());
		@SuppressWarnings("unchecked")
		List<String> configFileLines = FileUtils.readLines(actualFile);
		String[] configXMLLines =expectedContents.split("\r?\n|\r");
		for (int i=0;i<configXMLLines.length;i++) {
			String expectedLine = configXMLLines[i];
			String actualLine = configFileLines.get(i);
			// Replace TMPDIR line, and replace Windows-style backslash separators to forward slashes.
			if (expectedLine.contains("REPLACE_WITH_TMPDIR")) {
				expectedLine = expectedLine.replace("REPLACE_WITH_TMPDIR", replacementText);
				expectedLine = expectedLine.replace("\\", "/");
				actualLine = actualLine.replace("\\", "/");
			}
			try {
				assertThat(actualLine, is(expectedLine));
			}
			catch (IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException(String.format("→ line %s not found in calculated output.", expectedLine));
			}
		}
		assertThat(configFileLines.size(), is(configXMLLines.length));
	}
}
