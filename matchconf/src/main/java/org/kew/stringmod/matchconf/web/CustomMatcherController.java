package org.kew.stringmod.matchconf.web;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.stringmod.matchconf.Configuration;
import org.kew.stringmod.matchconf.Matcher;
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
public class CustomMatcherController {

    // GET: matcher creation form for this configuration
    @RequestMapping(value="/{configType}_configs/{configName}/matchers", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model uiModel,
                              @RequestParam(value = "className", required = false) String className,
                              @RequestParam(value = "packageName", required = false) String packageName) {
        Matcher instance = new Matcher();
        instance.setPackageName(packageName);
        instance.setClassName(className);
        populateEditForm(uiModel, configType, configName, instance);
        return "config_matchers/create";
    }

    // POST to create an object and add it to this configuration
    @RequestMapping(value="/{configType}_configs/{configName}/matchers", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Matcher matcher, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        // assert unique_together:config&name
        if (config.getMatcherForName(matcher.getName()) != null) {
            bindingResult.addError(new ObjectError("matcher.name", "There is already a Matcher set up for this configuration with this name."));
        }
        this.customValidation(configName, matcher, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, matcher);
            return "config_matchers/create";
        }
        uiModel.asMap().clear();
        matcher.setConfiguration(config);
        matcher.persist();
        config.getMatchers().add(matcher);
        config.merge();
        return "redirect:/{configType}_configs/" + encodeUrlPathSegment(configName, httpServletRequest) + "/matchers/" + encodeUrlPathSegment(matcher.getName(), httpServletRequest);
    }

    // GET indiviual matcher level
    @RequestMapping(value="/{configType}_configs/{configName}/matchers/{matcherName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("matcherName") String matcherName, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        Matcher matcher = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getMatcherForName(matcherName);
        uiModel.addAttribute("matcher", matcher);
        uiModel.addAttribute("itemId", matcher.getName());
        uiModel.addAttribute("configName", configName);
        return "config_matchers/show";
    }

    // GET list of matchers for this config
    @RequestMapping(value="/{configType}_configs/{configName}/matchers", produces = "text/html")
    public String list(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        List<Matcher> matchers = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getMatchers();
        if (page != null || size != null) {
            int sizeNo = Math.min(size == null ? 10 : size.intValue(), matchers.size());
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("matchers", matchers.subList(firstResult, sizeNo));
            float nrOfPages = (float) Matcher.countMatchers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("matchers", matchers);
        }
        uiModel.addAttribute("configName", configName);
        return "config_matchers/list";
    }

    // GET the update form for a specific matcher
    @RequestMapping(value="/{configType}_configs/{configName}/matchers/{matcherName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("matcherName") String matcherName, Model uiModel) {
        Matcher matcher = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getMatcherForName(matcherName);
        populateEditForm(uiModel, configType, configName, matcher);
        return "config_matchers/update";
    }

    // PUT: update a specific matcher
    @RequestMapping(value="/{configType}_configs/{configName}/matchers", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Matcher matcher, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(configName, matcher, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, matcher);
            return "config_matchers/update";
        }
        uiModel.asMap().clear();
        matcher.setConfiguration(Configuration.findConfigurationsByNameEquals(configName).getSingleResult());
        matcher.merge();
        return "redirect:/{configType}_configs/" + encodeUrlPathSegment(configName, httpServletRequest) + "/matchers/" + encodeUrlPathSegment(matcher.getName(), httpServletRequest);
    }

    // DELETE a specific matcher from a collection's list
    @RequestMapping(value="/{configType}_configs/{configName}/matchers/{matcherName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("matcherName") String matcherName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.removeMatcher(matcherName);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/{configType}_configs/" + configName.toString() + "/matchers/";
    }

    // DELETE by id
    @RequestMapping(value="/{configType}_configs/{configName}/matchers/delete-by-id/{id}", method = RequestMethod.GET, produces = "text/html")
    public String deleteById(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("id") long id, Model uiModel) {
        Matcher toDelete = Matcher.findMatcher(id);
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.getMatchers().remove(toDelete);
        config.merge();
        return "redirect:/{configType}_configs/" + configName.toString() + "/matchers/";
    }

    void populateEditForm(Model uiModel, String configType, String configName, Matcher matcher) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        List<Matcher> matchers = config.getMatchers();
        matchers.remove(matcher);
        uiModel.addAttribute("matcher", matcher);
        uiModel.addAttribute("matchers", matchers);
        uiModel.addAttribute("configType", configType);
        uiModel.addAttribute("configName", configName);
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

    public void customValidation (String configName, Matcher matcher, BindingResult br) {
        if (!FieldValidator.validSlug(matcher.getName())) {
            br.addError(new ObjectError("matcher.name", "The name has to be set and can only contain ASCII letters and/or '-' and/or '_'"));
        }
    }

}
