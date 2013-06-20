package org.kew.shs.dedupl.matchconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class CreateSimpleConfigOneMatcher {

    File tempDir;

    String secondColName;
    Transformer transformer;
    Matcher matcher;
    Configuration config;

    @Before
    public void before (Scenario scenario) throws Throwable {
        this.tempDir = Files.createTempDirectory("dedupTestDir").toFile();
    }

    @After()
    public void after (Scenario scenario) {
        tempDir.delete();
    }

    @Given("^Alecs has a file containing data in two columns, \\(\"([^\"]*)\", \"([^\"]*)\"\\) in a directory \"([^\"]*)\"$")
    public void Alecs_has_a_file_containing_data_in_two_columns_in_a_directory(String firstColName, String secondColName, String workDirPath) throws Throwable {
        // 'this' is the 'World' class all step-defs in one scenario share
        this.secondColName = secondColName;

        File workDir = new File(tempDir, workDirPath);
        workDir.mkdir();
        new File(workDir, "input.tsv").createNewFile();
    }

    @Given("^he has added a composite transformer for the first column:$")
    public void he_has_added_a_composite_transformer_for_the_first_column(DataTable transformerDefTable) throws Throwable {
        List<Map<String,String>> transformerDef = transformerDefTable.asMaps();
        this.transformer = new Transformer();
        this.transformer.setName(transformerDef.get(0).get("name"));
        this.transformer.setPackageName(transformerDef.get(0).get("packageName"));
        this.transformer.setClassName(transformerDef.get(0).get("className"));
        this.transformer.setParams(transformerDef.get(0).get("params"));
        this.transformer.persist();
    }

    @Given("^this Composite Transformer contains the following transformers$")
    public void this_Composite_Transformer_contains_the_following_transformers(DataTable transformerDefTable) throws Throwable {
        List<Transformer> components = new ArrayList<Transformer>();
        for (Map<String,String> transDef:transformerDefTable.asMaps()) {
            Transformer component = new Transformer();
            component.setName(transDef.get("name"));
            component.setPackageName(transDef.get("packageName"));
            component.setClassName(transDef.get("className"));
            component.setParams(transDef.get("params"));
            components.add(component);
        }
            this.transformer.setComposedBy(components);
    }

    @Given("^he has added a matcher for the first column:$")
    public void he_has_added_a_matcher_for_the_first_column(DataTable matcherDefTable) throws Throwable {
        List<Map<String,String>> matcherDef = matcherDefTable.asMaps();
        this.matcher = new Matcher();
        this.matcher.setName(matcherDef.get(0).get("name"));
        this.matcher.setPackageName(matcherDef.get(0).get("packageName"));
        this.matcher.setClassName(matcherDef.get(0).get("className"));
        this.matcher.setParams(matcherDef.get(0).get("params"));
        this.matcher.persist();
        assert (Matcher.findMatcher(this.matcher.getId()) != null);
    }

    @Given("^he has wired the matcher and the transformer to the column in a new configuration:$")
    public void he_has_wired_the_matcher_and_the_transformer_to_the_column_in_a_new_configuration(DataTable colDefTable) throws Throwable {
        List<Map<String,String>> colDef = colDefTable.asMaps();
        this.config = new Configuration();
        this.config.setName(colDef.get(0).get("name"));
        this.config.setWorkDirPath(new File(this.tempDir, colDef.get(0).get("workDirPath")).getPath());
        this.config.persist();
        assert (Configuration.findConfiguration(this.config.getId()) != null);
        Wire wire = new Wire();
        wire.setConfiguration(this.config);
        wire.setColumnName(this.secondColName);
        wire.setColumnIndex(1);
        wire.setMatcher(this.matcher);
        wire.persist();
        wire.getTransformer().add(this.transformer);
        wire = wire.merge();
        assert (Wire.findWire(wire.getId()) != null);
        this.matcher.getMatchedWires().add(wire);
        this.matcher.merge();
        this.config.getWiring().add(wire);
        this.config.merge();
        Set<Wire> wiring = new HashSet<Wire>();
        wiring.add(wire);
        assertThat(this.config.getWiring(), is(wiring));
    }

    @When("^he asks to write the configuration out to the filesystem$")
    public void he_asks_to_write_the_configuration_out_to_the_filesystem() throws Throwable {
        ConfigurationEngine configEngine = new ConfigurationEngine(this.config);
        configEngine.write_to_filesystem();
    }

    @Then("^the following content will be written to \"([^\"]*)\":$")
    public void the_following_content_will_be_written_to_(String configFilePath, String configXML) throws Throwable {
        File configFile = new File(this.tempDir, configFilePath);
        assert configFile.exists();
        List<String> configFileLines = FileUtils.readLines(configFile);
        String[] configXMLLines =configXML.split("\n");
        for (int i=0;i<configXMLLines.length;i++) {
            String correctedLine = configXMLLines[i].replaceAll("REPLACE_WITH_TMPDIR", this.tempDir.toString());
            try {
                assertThat(configFileLines.get(i), is(correctedLine));
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException(String.format("\n--> line %s not found in calculated output.", correctedLine));
            }
        }
        assertThat(configFileLines.size(), is(configXMLLines.length));
    }
}
