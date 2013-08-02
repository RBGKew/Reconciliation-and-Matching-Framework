package org.kew.shs.dedupl.matchconf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RunSimpleMatchConfig {

    File tempDir;
    File workDir;
    long configId;

    @Before
    public void before (Scenario scenario) throws Throwable {
        this.tempDir = Files.createTempDirectory("dedupTestDir").toFile();
    }

    @After()
    public void after (Scenario scenario) throws IOException {
        tempDir.delete();
		FileUtils.deleteDirectory(new File("target/deduplicator")); // removes the lucene index after scenario
    }


	@Given("^Alecs has set up a simple Configuration resulting in the following config \"([^\"]*)\" written to \"([^\"]*)\":$")
	public void Alecs_has_set_up_a_simple_Configuration_resulting_in_the_following_config_written_to_(String configFileName, String workDirPath, String configXML) throws Throwable {
		this.workDir = new File(workDirPath.replaceAll("REPLACE_WITH_TMPDIR", tempDir.toString()));
		Configuration config = new Configuration();
		config.setName("simple-config-to-run");
		config.setWorkDirPath(this.workDir.toString());
		config.persist();
		this.configId = config.getId();
		this.workDir.mkdirs();
		File configFile = new File(workDir, configFileName);
		configFile.createNewFile();
		String correctedConfigXML = configXML.replaceAll("REPLACE_WITH_TMPDIR", tempDir.toString());
		try (BufferedWriter br = new BufferedWriter(new FileWriter(configFile))) {
			br.write(correctedConfigXML);
		}
	}

	@Given("^some mysterious data-improver has put a file \"([^\"]*)\" in the same directory containing the following source data:$")
	public void some_mysterious_data_improver_has_put_a_file_in_the_same_directory_containing_the_following_source_data(String arg1, DataTable arg2) throws Throwable {
	    // Express the Regexp above with the code you wish you had
	    // For automatic conversion, change DataTable to List<YourType>
	    throw new PendingException();
	}

	@Given("^another mysterious data-improver has put a file \"([^\"]*)\" in the same directory containing the following lookup data:$")
	public void another_mysterious_data_improver_has_put_a_file_in_the_same_directory_containing_the_following_lookup_data(String arg1) throws Throwable {
	    // Express the Regexp above with the code you wish you had
	    throw new PendingException();
	}

	@When("^asking MatchConf to run this matching configuration$")
	public void asking_MatchConf_to_run_this_matching_configuration() throws Throwable {
	    // Express the Regexp above with the code you wish you had
	    throw new PendingException();
	}

	@Then("^the matching program should run smoothly and produce the following file \"([^\"]*)\" in the same directory:$")
	public void the_matching_program_should_run_smoothly_and_produce_the_following_file_in_the_same_directory(String arg1) throws Throwable {
	    // Express the Regexp above with the code you wish you had
	    throw new PendingException();
	}
}
