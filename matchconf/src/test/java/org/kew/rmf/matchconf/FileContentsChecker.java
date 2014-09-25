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
