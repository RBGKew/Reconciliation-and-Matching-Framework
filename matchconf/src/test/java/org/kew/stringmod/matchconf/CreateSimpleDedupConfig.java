package org.kew.stringmod.matchconf;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration(locations="classpath:/META-INF/spring/applicationContext.xml")
public class CreateSimpleDedupConfig {

    Logger logger = LoggerFactory.getLogger(CreateSimpleDedupConfig.class);
    File tempDir;

    String configName;
    String secondColName;
    Matcher matcher;
    List<Transformer> transformers;
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
        new File(workDir, "query.tsv").createNewFile();
    }

    @Given("^he has created a new configuration:$")
    public void he_has_created_a_new_configuration(DataTable colDefTable) throws Throwable {
        List<Map<String,String>> colDef = colDefTable.asMaps(String.class, String.class);
        this.configName = colDef.get(0).get("name");
        config = new Configuration();
        config.setName(this.configName);
        config.setMaxSearchResults(colDef.get(0).get("maxSearchResults"));
        config.setRecordFilter(colDef.get(0).get("recordFilter"));
        config.setNextConfig(colDef.get(0).get("nextConfig"));
        config.setWorkDirPath(new File(this.tempDir, colDef.get(0).get("workDirPath")).getPath());
        config.persist();
        assert (Configuration.findConfiguration(config.getId()) != null);
    }

    @Given("^he has added a dictionary \"([^\"]*)\" with the filepath field \"([^\"]*)\"$")
    public void he_has_added_a_dictionary_with_the_filepath_field(String name, String filePath) throws Throwable {
        Dictionary dict = new Dictionary();
        dict.setName(name);
        dict.setFilePath(filePath);
        dict.persist();
    }

    @Given("^he has added the following queryTransformers$")
    public void he_has_added_the_following_transformers(DataTable transformerDefTable) throws Throwable {
        List<Transformer> transis = new ArrayList<>();
        for (Map<String,String> transDef:transformerDefTable.asMaps(String.class, String.class)) {
            Transformer transi = new Transformer();
            transi.setName(transDef.get("name"));
            transi.setPackageName(transDef.get("packageName"));
            transi.setClassName(transDef.get("className"));
            transi.setParams(transDef.get("params"));
            transi.persist();
            transis.add(transi);
        }
        config.setTransformers(transis);
        this.transformers = new ArrayList<>(transis);
    }

    @Given("^he has added a matcher for the second column:$")
    public void he_has_added_a_matcher_for_the_second_column(DataTable matcherDefTable) throws Throwable {
        List<Map<String,String>> matcherDef = matcherDefTable.asMaps(String.class, String.class);
        matcher = new Matcher();
        matcher.setName(matcherDef.get(0).get("name"));
        matcher.setPackageName(matcherDef.get(0).get("packageName"));
        matcher.setClassName(matcherDef.get(0).get("className"));
        matcher.setParams(matcherDef.get(0).get("params"));
        matcher.setConfiguration(config);
        matcher.persist();
        List<Matcher> matchers = new ArrayList<>();
        matchers.add(matcher);
        config.getMatchers().add(matcher);
        config.setVersion(config.getVersion() + 1);
        assert (Matcher.findMatcher(this.matcher.getId()) != null);
    }

    @Given("^he has wired them together at the second column$")
    public void he_has_wired_the_together_at_the_second_column() throws Throwable {
        Wire wire = new Wire();
        wire.setQueryColumnName(this.secondColName);
        wire.setMatcher(this.matcher);
        wire.setConfiguration(config);
        wire.persist();
        int i = 0;
        for (Transformer t:this.transformers) {
            i ++;
            WiredTransformer wTrans = new WiredTransformer();
            wTrans.setRank(i);
            wTrans.setTransformer(t);
            wTrans.persist();
            wire.getQueryTransformers().add(wTrans);
        }
        wire.merge();
        assert (Wire.findWire(wire.getId()) != null);
        config.getWiring().add(wire);
        logger.info("config.getWiring(): {}, wire: {}", config.getWiring(), wire);
        // TODO: the following works in STS but not on the command-line, why?
        //assert (config.getWiring().toArray(new Wire[1]).equals(new Wire[] {wire}));
    }

    @Given("^he has added the following reporters:$")
        public void he_has_added_the_following_reporters(DataTable reporterDefTable) throws Throwable {
        List<Reporter> reps = new ArrayList<>();
        for (Map<String,String> repDef:reporterDefTable.asMaps(String.class, String.class)) {
            Reporter rep = new Reporter();
            rep.setName(repDef.get("name"));
            rep.setFileName(repDef.get("fileName"));
            rep.setPackageName(repDef.get("packageName"));
            rep.setClassName(repDef.get("className"));
            rep.setParams(repDef.get("params"));
            rep.setConfig(this.config);
            rep.persist();
            reps.add(rep);
        }
        config.setReporters(reps);
    }

    @When("^he asks to write the configuration out to the filesystem$")
    public void he_asks_to_write_the_configuration_out_to_the_filesystem() throws Throwable {
        ConfigurationEngine configEngine = new ConfigurationEngine(config);
        configEngine.write_to_filesystem();
    }

    @Then("^the following content will be written to \"([^\"]*)\":$")
    public void the_following_content_will_be_written_to_(String configFilePath, String configXML) throws Throwable {
        File configFile = new File(this.tempDir, configFilePath);
        FileContentsChecker.checkFilesSame(configFile, configXML, tempDir.toString());
    }
}
