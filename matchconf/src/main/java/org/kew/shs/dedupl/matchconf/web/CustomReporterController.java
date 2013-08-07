package org.kew.shs.dedupl.matchconf.web;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.Reporter;
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
public class CustomReporterController {

   @RequestMapping(value="/{configType}_configs/{configName}/reporters", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model uiModel,
                              @RequestParam(value = "className", required = false) String className,
                              @RequestParam(value = "packageName", required = false) String packageName) {
        Reporter instance = new Reporter();
        instance.setPackageName(packageName);
        instance.setClassName(className);
        populateEditForm(uiModel, configType, configName, instance);
        return "config_reporters/create";
    }

    // Default Post
    @RequestMapping(value="/{configType}_configs/{configName}/reporters", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Reporter reporter, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        // assert unique_together:config&name
        if (Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getReporterForName(reporter.getName()) != null) {
            bindingResult.addError(new ObjectError("reporter.name", "There is already a LuceneReporter set up for this configuration with this name."));
        }
        this.customValidation(configName, reporter, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, reporter);
            return "config_reporters/create";
        }
        uiModel.asMap().clear();
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        reporter.setConfig(config);
        reporter.persist();
        config.getReporters().add(reporter);
        config.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configName, httpServletRequest) + "/reporters/" + encodeUrlPathSegment(reporter.getName(), httpServletRequest);
    }

    // default GET indiviual reporter level
    @RequestMapping(value="/{configType}_configs/{configName}/reporters/{reporterName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("reporterName") String reporterName, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        Reporter reporter = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getReporterForName(reporterName);
        uiModel.addAttribute("reporter", reporter);
        uiModel.addAttribute("itemId", reporter.getName());
        uiModel.addAttribute("configName", configName);
        uiModel.addAttribute("transformers", reporter.getConfig().getTransformers());
        return "config_reporters/show";
    }

    @RequestMapping(value="/{configType}_configs/{configName}/reporters", produces = "text/html")
    public String list(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        List<Reporter> reporters = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getReporters();
        if (page != null || size != null) {
            int sizeNo = Math.min(size == null ? 10 : size.intValue(), reporters.size());
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("reporters", reporters.subList(firstResult, sizeNo));
            float nrOfPages = (float) reporters.size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("reporters", reporters);
        }
        uiModel.addAttribute("configName", configName);
        return "config_reporters/list";
    }

    @RequestMapping(value="/{configType}_configs/{configName}/reporters", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Reporter reporter, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(configName, reporter, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, reporter);
            return String.format("%s_config_reporters/update", configType);
        }
        uiModel.asMap().clear();
        reporter.setConfig(Configuration.findConfigurationsByNameEquals(configName).getSingleResult());
        reporter.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configName, httpServletRequest) + "/reporters/" + encodeUrlPathSegment(reporter.getName(), httpServletRequest);
    }

    @RequestMapping(value="/{configType}_configs/{configName}/reporters/{reporterName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("reporterName") String reporterName, Model uiModel) {
        Reporter reporter = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getReporterForName(reporterName);
        populateEditForm(uiModel, configType, configName, reporter);
        return "config_reporters/update";
    }

    @RequestMapping(value="/{configType}_configs/{configName}/reporters/{reporterName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("reporterName") String reporterName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.removeReporter(reporterName);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return String.format("redirect:/%s_configs/%s/reporters/", configType, configName.toString());
    }

    // DELETE by id
    @RequestMapping(value="/{configType}_configs/{configName}/reporters/delete-by-id/{id}", method = RequestMethod.GET, produces = "text/html")
    public String deleteById(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("id") long id, Model uiModel) {
        Reporter toDelete = Reporter.findReporter(id);
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.getReporters().remove(toDelete);
        config.merge();
        return "redirect:/{configType}_configs/" + configName.toString() + "/reporters/";
    }

    void populateEditForm(Model uiModel, String configType, String configName, Reporter reporter) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        uiModel.addAttribute("reporter", reporter);
        uiModel.addAttribute("configName", configName);
        uiModel.addAttribute("configType", configType);
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

    public void customValidation (String configName, Reporter reporter, BindingResult br) {
        if (!FieldValidator.validSlug(reporter.getName())) {
            br.addError(new ObjectError("reporter.name", "The name can only contain ASCII letters and/or '-' and/or '_'"));
        }
    }

}
