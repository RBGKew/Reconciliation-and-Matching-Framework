package org.kew.shs.dedupl.matchconf.web;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.ConfigurationEngine;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/configurations")
@Controller
@RooWebScaffold(path = "configurations", formBackingObject = Configuration.class)
public class ConfigurationController {

    @RequestMapping(value = "/{configName}/run", produces = "text/html")
    public String runConfig(@PathVariable("configName") String configName, Model model) throws IOException {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        try {
            ConfigurationEngine engine = new ConfigurationEngine(config);
            engine.write_to_filesystem();
            engine.runConfiguration();
            model.addAttribute("config", config);
        } catch (RuntimeException e) {
            model.addAttribute("exception", e.toString());
        } catch (Exception e) {
            model.addAttribute("exception", e.toString());
        } catch (Error e) {
            model.addAttribute("exception", e.toString());
        } finally {
                File luceneDir = new File("target/deduplicator");
                if (luceneDir.exists()) {
                    FileUtils.deleteDirectory(new File("target/deduplicator"));
	            }
	        return "configurations/run/index";
        }
    }
}
