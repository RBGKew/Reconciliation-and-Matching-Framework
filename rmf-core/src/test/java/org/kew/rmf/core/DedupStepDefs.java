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
package org.kew.rmf.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DedupStepDefs {
	//private final static Logger log = LoggerFactory.getLogger(DedupStepDefs.class);

	// define input and output files; like this we make them available as variables for the scope
	// of all steps in the scenario, but we actually create and delete them in a controlled way
	Path tempDir;
	Path tempConfigFile;
	Path tempQueryFile;
	Path tempAuthorityFile;
	Path tempOutputFile;
	Path tempOutputMultilineFile;
	Path dictFile;

	@Before
	public void before() throws Exception {
		tempDir = Files.createTempDirectory("dedup-dir");
		tempConfigFile = new File(tempDir + File.separator + "config.xml").toPath();
		tempQueryFile = new File(tempDir + File.separator + "query.txt").toPath();
		// tempAuthorityFile shall be used additionally for matching tasks..
		tempAuthorityFile = new File(tempDir + File.separator + "authority.txt").toPath();
		tempOutputFile = new File(tempDir + File.separator + "output.txt").toPath();
		tempOutputMultilineFile = new File(tempDir + File.separator + "output_multiline.txt").toPath();
		dictFile = new File(tempDir + File.separator + "funkyDict.txt").toPath();
	}

	@After
	public void after() throws Exception {
		FileUtils.deleteDirectory(tempDir.toFile());
		FileUtils.deleteDirectory(new File("target/deduplicator")); // removes the lucene index after scenario
	}

	private void writeDataTableToFile(DataTable dataTable, Path path) throws Exception {
		Writer w = new OutputStreamWriter(new FileOutputStream(path.toFile()), "UTF-8");
		for (List<String> rows : dataTable.cells(0)) {
			for (String cell : rows) {
				w.write(cell);
				w.write("\t");
			}
			w.write("\n");
		}
		w.close();
	}

	@Given("^(?:.*) has created an? (?:input|query)-file(?:.*)$")
	public void x_has_created_an_y_file(DataTable fileContent) throws Exception {
		writeDataTableToFile(fileContent, tempQueryFile);
	}

	@Given("^(?:he|she) has created an authority-file to match against$")
	public void has_created_a_authorityfile_to_match_against(DataTable fileContent) throws Exception {
		writeDataTableToFile(fileContent, tempAuthorityFile);
	}

	@Given("^(?:he|she) has access to a tab-separated dictionary$")
	public void has_access_to_a_tabseparated_dictionary(DataTable dictContent) throws Exception {
		writeDataTableToFile(dictContent, dictFile);
	}

	@Given("^Alecs has set up a (?:.*)configuration file(?:.*)$")
	public void alecs_has_set_up_a_configuration_file(String configXML) throws Exception {
		String escapedTempPath = tempDir.toString().replace("\\", "\\\\");
		Writer w = new OutputStreamWriter(new FileOutputStream(tempConfigFile.toFile()), "UTF-8");
		w.write(configXML.replaceAll("REPLACE_WITH_TMPDIR", escapedTempPath));
		w.close();
	}

	@When("^this config is run through the deduplicator$")
	public void this_config_is_run_through_the_deduplicator() throws Exception {
		String[] args = {"-d", tempDir.toString() + "/"};
		CoreApp.main(args);
	}

	private void checkDataInFile(DataTable expectedOutput, Path path) throws Exception {
		List<List<String>> expectedOutputList = expectedOutput.cells(0);
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(path.toFile(), "UTF-8");

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			List<String> expectedLine = expectedOutputList.get(i);
			List<String> lineList = Arrays.asList(line.split("\t", expectedLine.size()));

			Assert.assertArrayEquals("Error occurred in record "+i, expectedLine.toArray(), lineList.toArray());
		}
	}

	@Then("^a file should have been created in the same folder with the following data:$")
	public void a_file_should_have_been_created_in_the_same_folder_with_the_following_data(DataTable expectedOutput) throws Exception {
		checkDataInFile(expectedOutput, tempOutputFile);
	}

	@Then("^a file for the multiline output should have been created in the same folder with the following data:$")
	public void a_file_for_the_multiline_output_should_have_been_created_in_the_same_folder_with_the_following_data(DataTable expectedOutput) throws Exception {
		checkDataInFile(expectedOutput, tempOutputMultilineFile);
	}
}
