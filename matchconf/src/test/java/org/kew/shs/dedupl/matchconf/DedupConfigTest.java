package org.kew.shs.dedupl.matchconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import cucumber.api.java.After;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
public class DedupConfigTest extends AbstractJUnit4SpringContextTests {

    Configuration config;

    Transformer trans1;
    Transformer trans2;

    Matcher matcher1;
    Matcher matcher2;

    Wire wire1;
    Wire wire2;

    Reporter rep1;
    Reporter rep2;

    @Before
    public void createTestMatchConfig() {
        this.config = new Configuration();
        this.config.setName("Clara");
        this.config.setWorkDirPath("/some/file/path");
        this.config.setRecordFilter("some_funny_javascript");
        this.config.setNextConfig("optional_name_of_the_config_to_run_afterwards");
        this.config.persist();

        // trans1 has no parameter set
        this.trans1 = new Transformer();
        this.trans1.setName("HipsterTransformer");
        this.trans1.setPackageName("lon.don.people");
        this.trans1.setClassName("lowerMiddleClass");
        this.trans1.setConfiguration(this.config);
        this.trans1.persist();
        // trans2 has a parameter
        this.trans2 = new Transformer();
        this.trans2.setName("poshGuy");
        this.trans2.setPackageName("lon.don.people");
        this.trans2.setClassName("veryUpperClass");
        this.trans2.setParams("hairStyle=uebercrazy");
        this.trans2.setConfiguration(this.config);
        this.trans2.persist();

        // matcher1 doesn't have any params set
        this.matcher1 = new Matcher();
        this.matcher1.setName("Hans");
        this.matcher1.setPackageName("a.long.and.funny.javapackagename");
        this.matcher1.setClassName("SomeHalfUnreadableCamelCasedName");
        this.matcher1.setConfiguration(this.config);
        this.matcher1.persist();
        // matcher2 does have params
        this.matcher2 = new Matcher();
        this.matcher2.setName("Klaus");
        this.matcher2.setPackageName("a.long.and.funny.javapackagename");
        this.matcher2.setClassName("KlausDieMaus");
        this.matcher2.setParams("matchEverything=true");
        this.matcher2.setConfiguration(this.config);
        this.matcher2.persist();

        this.wire1 = new Wire();
        this.wire1.setSourceColumnName("sauceColumn");
        this.wire1.setConfiguration(this.config);
        // wire1 has blanksMatch set to true
        this.wire1.setBlanksMatch(true);
        // wire1 has only one transformer
        this.wire1.getSourceTransformers().add(this.trans1);
        this.wire1.setMatcher(this.matcher1);
        this.wire1.persist();

        // wire1 has only one transformer
        this.wire2 = new Wire();
        this.wire2.setSourceColumnName("saladColumn");
        this.wire2.setConfiguration(this.config);
        // wire2 has *not* blanksMatch set to true
        // wire2 has two transformers
        this.wire2.getSourceTransformers().add(this.trans1);
        this.wire2.getSourceTransformers().add(this.trans2);
        this.wire2.setMatcher(this.matcher2);
        this.wire2.persist();

        this.rep1 = new Reporter();
        this.rep1.setName("karkaKolumna");
        this.rep1.setPackageName("lon.don.people.busy");
        this.rep1.setClassName("TelegraphReporter");
        this.rep1.setFileName("someFunnyFile");
        this.rep1.setConfig(this.config);

        this.rep2 = new Reporter();
        this.rep2.setName("flyingBulldog");
        this.rep2.setPackageName("lon.don.people.busy");
        this.rep2.setClassName("SunReporter");
        this.rep2.setFileName("someOtherFunnyFile");
        this.rep2.setConfig(this.config);

        this.config.getTransformers().add(this.trans1);
        this.config.getTransformers().add(this.trans2);
        this.config.getMatchers().add(this.matcher1);
        this.config.getMatchers().add(this.matcher2);
        this.config.getReporters().add(this.rep1);
        this.config.getReporters().add(this.rep2);
        this.config.getWiring().add(this.wire1);
        this.config.getWiring().add(this.wire2);

        this.config.merge();
    }

    @After
    public void deleteTestConfig() {
        try {
            this.config.remove();
        } catch (NullPointerException e) {};
    }


    @Test
    public void testClone() {
        Configuration clone = this.config.clone();
        assertThat(clone.getName(), equalTo("copy-of_" + config.getName()));
        assertThat(clone.getId(), not(equalTo(config.getId())));
        assertThat(clone.getTransformers().get(0).getName(), equalTo(this.trans1.getName()));
        assertThat(clone.getTransformers().get(0).getId(), not(equalTo(this.trans1.getId())));
        assertThat(clone.getTransformers().get(1).getName(), equalTo(this.trans2.getName()));
    }

}
