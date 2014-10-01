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

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Given("^Alecs has a query-file containing data in three columns, \\(\"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"\\) in a directory \"([^\"]*)\"$")
    public void Alecs_has_a_query_file_containing_data_in_three_columns_in_a_directory(String firstColName, String secondColName, String thirdColName, String workDirPath) throws Throwable {
        this.workDir = new File(tempDir, workDirPath);
        this.workDir.mkdir();
        new File(this.workDir, "query.tsv").createNewFile();
    }

    @Given("^Alecs has an authority-file containing data in three columns \\(\"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"\\) in the same directory$")
    public void Alecs_has_an_authority_file_containing_data_in_three_columns_in_the_same(String firstColName, String secondColName, String thirdColName) throws Throwable {
        new File(this.workDir, "authority.tsv").createNewFile();
    }

    @Given("^he has created a new match configuration:$")
    public void he_has_created_a_new_match_configuration(DataTable colDefTable) throws Throwable {
        List<Map<String,String>> colDef = colDefTable.asMaps(String.class, String.class);
        this.configName = colDef.get(0).get("name");
        config = new Configuration();
        config.setName(this.configName);
        config.setClassName("MatchConfiguration");
        config.setWorkDirPath(new File(this.tempDir, colDef.get(0).get("workDirPath")).getPath());
        config.setAuthorityFileName("authority.tsv");
        config.persist();
        assert (Configuration.findConfiguration(config.getId()) != null);
    }

    @Given("^he has added the following query- and authorityTransformers$")
    public void he_has_added_the_following_query_and_authorityTransformers(DataTable transformerDefTable) throws Throwable {
        List<Transformer> transis = new ArrayList<>();
        for (Map<String,String> transDef:transformerDefTable.asMaps(String.class, String.class)) {
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
        for (Map<String,String> matcherDef:matcherDefTable.asMaps(String.class, String.class)) {
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
        for (Map<String,String> wireDef:wireDefTable.asMaps(String.class, String.class)) {
            Wire wire = new Wire();
            wire.setQueryColumnName(wireDef.get("queryColumnName"));
            wire.setAuthorityColumnName(wireDef.get("authorityColumnName"));
            wire.setUseInSelect(Boolean.parseBoolean(wireDef.get("useInSelect")));
            wire.setMatcher(this.matchers.get(wireDef.get("matcher")));
            wire.setConfiguration(config);
            wire.persist();
            int i = 0;
            for (String t:wireDef.get("queryTransformers").split(",")) {
                i ++;
                WiredTransformer wTrans = new WiredTransformer();
                // reverse the order to test whether the rank is actually used for sorting
                // and not only the auto-generated id
                wTrans.setRank(i * - 1);
                Transformer transf = this.transformers.get(t.trim());
                wTrans.setTransformer(transf);
                wTrans.persist();
                wire.getQueryTransformers().add(wTrans);
            }
            i = 0;
            for (String t:wireDef.get("authorityTransformers").split(",")) {
                i ++;
                WiredTransformer wTrans = new WiredTransformer();
                // reverse the order to test whether the rank is actually used for sorting
                // and not only the auto-generated id
                wTrans.setRank(i * - 1);
                Transformer transf = this.transformers.get(t.trim());
                wTrans.setTransformer(transf);
                wTrans.persist();
                wire.getAuthorityTransformers().add(wTrans);
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

    @When("^he asks to write the match-configuration out to the filesystem$")
    public void he_asks_to_write_the_configuration_out_to_the_filesystem() throws Throwable {
        ConfigurationEngine configEngine = new ConfigurationEngine(config);
        configEngine.write_to_filesystem();
    }

    @Then("^the following match-config will be written to \"([^\"]*)\":$")
    public void the_following_content_will_be_written_to_(String configFilePath, String configXML) throws Throwable {
        File configFile = new File(this.tempDir, configFilePath);
        FileContentsChecker.checkFilesSame(configFile, configXML, tempDir.toString());
    }
}
