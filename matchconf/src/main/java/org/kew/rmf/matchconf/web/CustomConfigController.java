package org.kew.rmf.matchconf.web;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.rmf.matchconf.Configuration;
import org.kew.rmf.matchconf.ConfigurationEngine;
import org.kew.rmf.matchconf.Transformer;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;


@Controller
public class CustomConfigController {

    // redirect root (/MatchConf) to default GET configs overview
    @RequestMapping(value = "/", produces = "text/html")
    public String root() {
        return "redirect:/dedup_configs";
    }

    // default GET configs overview
    @RequestMapping(value = "/{configType}_configs", produces = "text/html")
    public String list(@PathVariable("configType") String configType,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "size", required = false) Integer size, Model uiModel)
                        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        List<Configuration> configurations;
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            configurations = ConfigSwitch.findConfigEntries(firstResult, sizeNo, configType);
            float nrOfPages = (float) ConfigSwitch.countConfigs(configType) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            configurations = ConfigSwitch.findAllConfigs(configType);
        }
        uiModel.addAttribute("configurations", configurations);
        return String.format("%s_configs/list", configType);
    }

    // Default POST for configs
    @RequestMapping(value = "/{configType}_configs", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType,
                                     @Valid Configuration configuration, BindingResult bindingResult,
                                     Model uiModel, HttpServletRequest httpServletRequest) {
        configuration.setClassName(ConfigSwitch.TYPE_CLASS_MAP.get(configType));
        this.customValidation(configuration, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configuration);
            return configType + "_configs/create";
        }
        uiModel.asMap().clear();
        configuration.persist();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configuration.getName(), httpServletRequest);
    }

    @RequestMapping(value = "/{configType}_configs", params = "form", produces = "text/html")
    public String create(@PathVariable String configType, Model uiModel) {
        populateEditForm(uiModel, new Configuration());
        return configType + "_configs/create";
    }

    // Default GET indiviual config level
    @RequestMapping(value = "/{configType}_configs/{configName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        uiModel.addAttribute("configuration", config);
        uiModel.addAttribute("itemId", config.getName());
        return configType + "_configs/show";
    }

    @RequestMapping(value = "/{configType}_configs", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable String configType, @Valid Configuration configuration, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        configuration.setClassName(ConfigSwitch.TYPE_CLASS_MAP.get(configType));
        this.customValidation(configuration, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configuration);
            return configType + "_configs/update";
        }
        uiModel.asMap().clear();
        configuration.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configuration.getName(), httpServletRequest);
    }

    @RequestMapping(value = "/{configType}_configs/{configName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable String configType, @PathVariable("configName") String configName, Model uiModel) {
        populateEditForm(uiModel, Configuration.findConfigurationsByNameEquals(configName).getSingleResult());
        return configType + "_configs/update";
    }

    @RequestMapping(value = "/{configType}_configs/{configName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable String configType, @PathVariable("configName") String configName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) throws Exception {
        Configuration configuration = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        try {
            for (Transformer t:configuration.getTransformers()) t.removeOrphanedWiredTransformers(); // this is expensive and should go soon
            configuration.remove();
        } catch (JpaSystemException e) {
            // assuming a possibly still exotically ocurring legacy error: a wire
            // of a *different* config uses this matcher, hence it would have a
            // foreign key into the void and complains so the matcher can't be
            // deleted and this config neither..
            configuration.fixMatchersForAlienWire();
            configuration.getWiring().clear();
            configuration.merge();
            configuration.removeTransformers();
            configuration = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
            configuration.remove();
        }
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return String.format("redirect:/%s_configs", configType);
    }

    // DELETE by id
    @RequestMapping(value="/{configType}_configs/delete-by-id/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String deleteById(@PathVariable("configType") String configType, @PathVariable("id") long id, Model uiModel) {
        Configuration toDelete = Configuration.findConfiguration(id);
        toDelete.remove();
        return "redirect:/{configType}_configs/";
    }

    void populateEditForm(Model uiModel, Configuration configuration) {
        uiModel.addAttribute("configuration", configuration);
        uiModel.addAttribute("configName", configuration.getName());
        uiModel.addAttribute("wiring", configuration.getWiring());
        uiModel.addAttribute("transformers", configuration.getTransformers());
        uiModel.addAttribute("matchers", configuration.getMatchers());
        uiModel.addAttribute("reporters", configuration.getReporters());
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

    public void customValidation (Configuration config, BindingResult br) {
        // validation for all classes that have their name as part of a REST-ish url
        if (!FieldValidator.validSlug(config.getName())) {
            br.addError(new ObjectError("config.name", "The name has to be set and can only contain ASCII letters and/or '-' and/or '_'"));
        }
        // MatchConfig specific: has to specify a authorityFileName
        if (config.getClassName().equals("MatchConfiguration") && config.getAuthorityFileName().equals("")) {
            br.addError(new ObjectError("configuration.authorityFileName", "A Match Configuration needs to specify an authority File"));
        }
    }
    @RequestMapping(value = "/{configType}_configs/{configName}/clone", produces = "text/html")
    public String cloneConfig(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model model) throws Exception {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.cloneMe();
        return String.format("redirect:/%s_configs", configType);
    }
    @RequestMapping(value = "/{configType}_configs/{configName}/run", produces = "text/html")
    public String runConfig(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model model) throws Exception {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        Map<String, List<String>> infoMap = new ConfigurationEngine(config).runConfiguration();
        model.addAttribute("engineMessages", infoMap.get("messages"));
        model.addAttribute("config", config);
        model.addAttribute("exception", infoMap.get("exception"));
        model.addAttribute("stackTrace", infoMap.get("stackTrace"));
        return "configurations/run/index";
    }

}
