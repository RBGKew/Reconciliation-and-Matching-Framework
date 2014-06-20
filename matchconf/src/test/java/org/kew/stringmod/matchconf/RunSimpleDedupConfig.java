package org.kew.stringmod.matchconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration(locations="classpath:/META-INF/spring/applicationContext.xml")
public class RunSimpleDedupConfig {

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


	@Given("^Alecs has set up a simple Configuration resulting in the following config \"([^\"]*)\":$")
	public void Alecs_has_set_up_a_simple_Configuration_resulting_in_the_following_config_written_to_(String configFileName, String configXML) throws Throwable {
		this.workDir = new File(tempDir.getAbsolutePath(), "workDir");
		Configuration config = new Configuration();
		config.setName("simple-config-to-run");
		config.setWorkDirPath(this.workDir.toString());
		config.persist();
		this.configId = config.getId();
		this.workDir.mkdirs();
		File configFile = new File(workDir, configFileName);
		configFile.createNewFile();
		String correctedConfigXML = configXML.replaceAll("REPLACE_WITH_TMPDIR", workDir.toString());
		try (BufferedWriter br = new BufferedWriter(new FileWriter(configFile))) {
			br.write(correctedConfigXML);
		}
	}

	@Given("^some mysterious data-improver has put a file \"([^\"]*)\" in the same directory containing the following data:$")
	public void some_mysterious_data_improver_has_put_a_file_in_the_same_directory_containing_the_following_data(String inputFileName, DataTable inputFileContent) throws Throwable {
		File inputFile = new File(this.workDir, inputFileName);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile))) {
			for (List<String> line:inputFileContent.raw()) {
				bw.write(StringUtils.join(line, "\t") + System.getProperty("line.separator"));
			}
		}
	}

	@When("^asking MatchConf to run this configuration$")
	public void asking_MatchConf_to_run_this_configuration() throws Throwable {
		new ConfigurationEngine(Configuration.findConfiguration(this.configId)).runConfiguration(false);
	}

	@Then("^the deduplication program should run smoothly and produce the following file \"([^\"]*)\" in the same directory:$")
	public void the_deduplication_program_should_run_smoothly_and_produce_the_following_file_at_REPLACE_WITH_TMPDIR_some_path_output_csv_(String outputFileName, DataTable outputFileContent) throws Throwable {
		File outputFile = new File(this.workDir, outputFileName);
		assert outputFile.exists();
		int index = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				List<String> compareLine = outputFileContent.raw().get(index);
				assertThat(line, is(StringUtils.join(compareLine, "\t")));
				index += 1;
			}
		}

	}
}
