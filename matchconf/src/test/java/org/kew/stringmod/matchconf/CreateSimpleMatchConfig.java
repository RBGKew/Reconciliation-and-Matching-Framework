package org.kew.stringmod.matchconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.kew.stringmod.matchconf.Configuration;
import org.kew.stringmod.matchconf.ConfigurationEngine;
import org.kew.stringmod.matchconf.Matcher;
import org.kew.stringmod.matchconf.Reporter;
import org.kew.stringmod.matchconf.Transformer;
import org.kew.stringmod.matchconf.Wire;
import org.kew.stringmod.matchconf.WiredTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class CreateSimpleMatchConfig {

    Logger logger = LoggerFactory.getLogger(CreateSimpleDedupConfig.class);
    File tempDir;

    String configName;
    String secondColName;
    String thirdColName;
    Map<String, Transformer> transformers = new HashMap<>();
    Map<String, Matcher> matchers = new HashMap<>();
    List<Reporter> reporters;
    Configuration config;
    File workDir;


    @Before
    public void before (Scenario scenario) throws Throwable {
        this.tempDir = Files.createTempDirectory("dedupTestDir").toFile();
    }

    @After()
    public void after (Scenario scenario) {
        tempDir.delete();
    }

    @Given("^Alecs has a source-file containing data in three columns, \\(\"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"\\) in a directory \"([^\"]*)\"$")
    public void Alecs_has_a_source_file_containing_data_in_three_columns_in_a_directory(String firstColName, String secondColName, String thirdColName, String workDirPath) throws Throwable {
        this.workDir = new File(tempDir, workDirPath);
        this.workDir.mkdir();
        new File(this.workDir, "source.tsv").createNewFile();
    }

    @Given("^Alecs has a lookup-file containing data in three columns \\(\"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"\\) in the same directory$")
    public void Alecs_has_a_lookup_file_containing_data_in_three_columns_in_the_same(String firstColName, String secondColName, String thirdColName) throws Throwable {
        new File(this.workDir, "lookup.tsv").createNewFile();
    }

    @Given("^he has created a new match configuration:$")
    public void he_has_created_a_new_match_configuration(DataTable colDefTable) throws Throwable {
        List<Map<String,String>> colDef = colDefTable.asMaps();
        this.configName = colDef.get(0).get("name");
        config = new Configuration();
        config.setName(this.configName);
        config.setClassName("MatchConfiguration");
        config.setWorkDirPath(new File(this.tempDir, colDef.get(0).get("workDirPath")).getPath());
        config.setLookupFileName("lookup.tsv");
        config.persist();
        assert (Configuration.findConfiguration(config.getId()) != null);
    }

    @Given("^he has added the following source- and lookupTransformers$")
    public void he_has_added_the_following_source_and_lookupTransformers(DataTable transformerDefTable) throws Throwable {
        List<Transformer> transis = new ArrayList<>();
        for (Map<String,String> transDef:transformerDefTable.asMaps()) {
            Transformer transi = new Transformer();
            transi.setName(transDef.get("name"));
            transi.setPackageName(transDef.get("packageName"));
            transi.setClassName(transDef.get("className"));
            transi.setParams(transDef.get("params"));
            transi.persist();
            transis.add(transi);
            this.transformers.put(transi.getName(), transi);
        }
        config.setTransformers(transis);
    }

    @Given("^he has added two matchers:$")
    public void he_has_added_two_matchers(DataTable matcherDefTable) throws Throwable {
        for (Map<String,String> matcherDef:matcherDefTable.asMaps()) {
            Matcher matcher = new Matcher();
            matcher.setName(matcherDef.get("name"));
            matcher.setPackageName(matcherDef.get("packageName"));
            matcher.setClassName(matcherDef.get("className"));
            matcher.setParams(matcherDef.get("params"));
            matcher.setConfiguration(config);
            matcher.persist();
            matchers.put(matcher.getName(), matcher);
            config.getMatchers().add(matcher);
            config.setVersion(config.getVersion() + 1);
            assert (Matcher.findMatcher(matcher.getId()) != null);
        }
    }

    @Given("^he has wired them together in the following way:$")
    public void he_has_wired_them_together_in_the_following_way(DataTable wireDefTable) throws Throwable {
        for (Map<String,String> wireDef:wireDefTable.asMaps()) {
            Wire wire = new Wire();
            wire.setSourceColumnName(wireDef.get("sourceColumnName"));
            wire.setLookupColumnName(wireDef.get("lookupColumnName"));
            wire.setUseInSelect(Boolean.parseBoolean(wireDef.get("useInSelect")));
            wire.setMatcher(this.matchers.get(wireDef.get("matcher")));
            wire.setConfiguration(config);
            wire.persist();
            int i = 0;
            for (String t:wireDef.get("sourceTransformers").split(",")) {
                i ++;
                WiredTransformer wTrans = new WiredTransformer();
                // reverse the order to test whether the rank is actually used for sorting
                // and not only the auto-generated id
                wTrans.setRank(i * - 1);
                Transformer transf = this.transformers.get(t.trim());
                wTrans.setTransformer(transf);
                wTrans.persist();
                wire.getSourceTransformers().add(wTrans);
            }
            i = 0;
            for (String t:wireDef.get("lookupTransformers").split(",")) {
                i ++;
                WiredTransformer wTrans = new WiredTransformer();
                // reverse the order to test whether the rank is actually used for sorting
                // and not only the auto-generated id
                wTrans.setRank(i * - 1);
                Transformer transf = this.transformers.get(t.trim());
                wTrans.setTransformer(transf);
                wTrans.persist();
                wire.getLookupTransformers().add(wTrans);
            }
            wire.merge();
            assert (Wire.findWire(wire.getId()) != null);
            config.getWiring().add(wire);
            logger.info("config.getWiring(): {}, wire: {}", config.getWiring(), wire);
        }
    }

    @Given("^he has added the following match-reporters:$")
        public void he_has_added_the_following_reporters(DataTable reporterDefTable) throws Throwable {
        List<Reporter> reps = new ArrayList<>();
        for (Map<String,String> repDef:reporterDefTable.asMaps()) {
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

    @When("^he asks to write the match-configuration out to the filesystem$")
    public void he_asks_to_write_the_configuration_out_to_the_filesystem() throws Throwable {
        ConfigurationEngine configEngine = new ConfigurationEngine(config);
        configEngine.write_to_filesystem();
    }

    @Then("^the following match-config will be written to \"([^\"]*)\":$")
    public void the_following_content_will_be_written_to_(String configFilePath, String configXML) throws Throwable {
        File configFile = new File(this.tempDir, configFilePath);
        assert configFile.exists();
        @SuppressWarnings("unchecked")
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
