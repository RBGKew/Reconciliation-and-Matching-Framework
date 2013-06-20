package org.kew.shs.dedupl.matchconf.web;
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
    public String runConfig(@PathVariable("configName") String configName, Model model) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        try {
            ConfigurationEngine engine = new ConfigurationEngine(config);
            engine.write_to_filesystem();
            engine.runConfiguration();
            model.addAttribute("config", config);
            return "configurations/run/index";
        } catch (RuntimeException e) {
            model.addAttribute("exception", e.toString());
            return "configurations/run/index";
        } catch (Exception e) {
            model.addAttribute("exception", e.toString());
            return "configurations/run/index";
        } catch (Error e) {
            model.addAttribute("exception", e.toString());
            return "configurations/run/index";
        }
    }
}
