package org.kew.stringmod.matchconf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kew.stringmod.matchconf.Bot;
import org.kew.stringmod.matchconf.Configuration;
import org.kew.stringmod.matchconf.Matcher;
import org.kew.stringmod.matchconf.Reporter;
import org.kew.stringmod.matchconf.Transformer;
import org.kew.stringmod.matchconf.Wire;
import org.kew.stringmod.matchconf.WiredTransformer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
public class DedupConfigTest extends AbstractJUnit4SpringContextTests {

    // the entities in our test-config-environment
    Configuration config;
    Transformer trans1;
    Transformer trans2;
    WiredTransformer wTrans1;
    WiredTransformer wTrans2;
    WiredTransformer wTrans3;
    Matcher matcher1;
    Matcher matcher2;
    Wire wire1;
    Wire wire2;
    Reporter rep1;
    Reporter rep2;

    public void createTestConfig(String name) {
        this.config = new Configuration();
        this.config.setName(name);
        this.config.setWorkDirPath("/some/file/path");
        this.config.setRecordFilter("some_funny_javascript");
        this.config.setNextConfig("optional_name_of_the_config_to_run_afterwards");

        // trans1 has a parameter
        this.trans1 = new Transformer();
        this.trans1.setName("poshGuy");
        this.trans1.setPackageName("lon.don.people");
        this.trans1.setClassName("veryUpperClass");
        this.trans1.setParams("hairStyle=uebercrazy");
        this.trans1.setConfiguration(this.config);
        // trans2 has no parameter set
        this.trans2 = new Transformer();
        this.trans2.setName("hipsterTransformer");
        this.trans2.setPackageName("lon.don.people");
        this.trans2.setClassName("lowerMiddleClass");
        this.trans2.setConfiguration(this.config);

        // matcher1 doesn't have any params set
        this.matcher1 = new Matcher();
        this.matcher1.setName("Hans");
        this.matcher1.setPackageName("a.long.and.funny.javapackagename");
        this.matcher1.setClassName("SomeHalfUnreadableCamelCasedName");
        this.matcher1.setConfiguration(this.config);
        // matcher2 does have params
        this.matcher2 = new Matcher();
        this.matcher2.setName("Klaus");
        this.matcher2.setPackageName("a.long.and.funny.javapackagename");
        this.matcher2.setClassName("KlausDieMaus");
        this.matcher2.setParams("matchEverything=true");
        this.matcher2.setConfiguration(this.config);

        this.wire1 = new Wire();
        this.wire1.setSourceColumnName("sauceColumn");
        this.wire1.setConfiguration(this.config);
        // wire1 has blanksMatch set to true
        this.wire1.setBlanksMatch(true);
        this.wire1.setMatcher(this.matcher1);

        this.wTrans1 = new WiredTransformer();
        this.wTrans1.setRank(1);
        this.wTrans1.setTransformer(this.trans1);
        // wire1 has only one transformer
        this.wire1.getSourceTransformers().add(this.wTrans1);

        this.wire2 = new Wire();
        this.wire2.setSourceColumnName("saladColumn");
        this.wire2.setConfiguration(this.config);
        // wire2 has *not* blanksMatch set to true
        this.wire2.setMatcher(this.matcher2);

        // wire2 has two transformers
        this.wTrans2 = new WiredTransformer();
        this.wTrans2.setRank(2);
        this.wTrans2.setTransformer(this.trans1);
        this.wire2.getSourceTransformers().add(this.wTrans2);

        this.wTrans3 = new WiredTransformer();
        this.wTrans3.setRank(1);
        this.wTrans3.setTransformer(this.trans2);
        this.wire2.getSourceTransformers().add(this.wTrans3);

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

        this.config.persist();
    }

    @Test
    public void deleteConfig() {
        createTestConfig("Igor");
        this.config = Configuration.findConfiguration(this.config.getId());
        this.config.remove();
    }

    @Test
    public void testSortBots() {
        createTestConfig("Zetkin");
        List<Transformer> transformers = this.config.getTransformers();
        Collections.sort(transformers);
        // h comes before p
        assertThat(transformers.get(0).getName(), equalTo("hipsterTransformer"));
    }

    public static String getattr(String method, Configuration aConfig) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return (String) aConfig.getClass().getMethod(method).invoke(aConfig);
    }

    @Test
    public void testCloneMe() throws Exception {
        createTestConfig("Clara");
        Configuration clone = this.config.cloneMe();
        // config
        assertThat(clone.getId(), not(equalTo(this.config.getId())));
        assertThat(clone.getName(), equalTo("copy-of_" + this.config.getName()));
        for (String fieldName:Configuration.CLONE_STRING_FIELDS) {
            assertThat(clone.getattr(fieldName, ""), equalTo(this.config.getattr(fieldName, "")));
        }
        // bots (transformers and matchers)
        assertThat(clone.getTransformers().get(0).getId(), not(equalTo(this.trans1.getId())));
        assertThat(clone.getTransformers().get(1).getId(), not(equalTo(this.trans2.getId())));
        assertThat(clone.getMatchers().get(0).getId(), not(equalTo(this.matcher1.getId())));
        assertThat(clone.getMatchers().get(0).getId(), not(equalTo(this.matcher2.getId())));
        for (String fieldName:Bot.CLONE_STRING_FIELDS) {
            assertThat(clone.getTransformers().get(0).getattr(fieldName, ""), equalTo(this.trans1.getattr(fieldName, "")));
            assertThat(clone.getTransformers().get(1).getattr(fieldName, ""), equalTo(this.trans2.getattr(fieldName, "")));
            assertThat(clone.getMatchers().get(0).getattr(fieldName, ""), equalTo(this.matcher1.getattr(fieldName, "")));
            assertThat(clone.getMatchers().get(1).getattr(fieldName, ""), equalTo(this.matcher2.getattr(fieldName, "")));
        }
        // reporters
        assertThat(clone.getReporters().get(0).getId(), not(equalTo(this.rep1.getId())));
        assertThat(clone.getReporters().get(0).getId(), not(equalTo(this.rep2.getId())));
        for (String fieldName:Reporter.CLONE_STRING_FIELDS) {
            assertThat(clone.getReporters().get(0).getattr(fieldName, ""), equalTo(this.rep1.getattr(fieldName, "")));
            assertThat(clone.getReporters().get(1).getattr(fieldName, ""), equalTo(this.rep2.getattr(fieldName, "")));
        }
        // wiring
        assertThat(clone.getWiring().get(0).getId(), not(equalTo(this.wire1.getId())));
        assertThat(clone.getWiring().get(0).getId(), not(equalTo(this.wire2.getId())));
        assertThat(clone.getWiring().get(0).getMatcher().getConfiguration().getId(), not(equalTo(this.config.getId())));
        assertThat(clone.getWiring().get(1).getMatcher().getConfiguration().getId(), not(equalTo(this.wire1.getMatcher().getConfiguration().getId())));
        for (String fieldName:Wire.CLONE_STRING_FIELDS) {
            assertThat(clone.getWiring().get(0).getattr(fieldName, ""), equalTo(this.wire1.getattr(fieldName, "")));
            assertThat(clone.getWiring().get(1).getattr(fieldName, ""), equalTo(this.wire2.getattr(fieldName, "")));
        }
        for (String fieldName:Wire.CLONE_BOOL_FIELDS) {
            assertThat(clone.getWiring().get(0).getattr(fieldName, true), equalTo(this.wire1.getattr(fieldName, true)));
            assertThat(clone.getWiring().get(1).getattr(fieldName, true), equalTo(this.wire2.getattr(fieldName, true)));
        }
    }

}
