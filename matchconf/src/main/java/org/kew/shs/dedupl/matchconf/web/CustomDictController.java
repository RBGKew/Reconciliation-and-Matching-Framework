package org.kew.shs.dedupl.matchconf.web;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.Dictionary;
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
public class CustomDictController {

   @RequestMapping(value="/{configType}_configs/{configName}/dicts", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model uiModel,
                              @RequestParam(value = "className", required = false) String className,
                              @RequestParam(value = "packageName", required = false) String packageName) {
        Dictionary instance = new Dictionary();
        populateEditForm(uiModel, configType, configName, instance);
        return "config_dicts/create";
    }

    // Default Post
    @RequestMapping(value="/{configType}_configs/{configName}/dicts", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Dictionary dict, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        // assert unique_together:config&name
        if (Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getDictionaryForName(dict.getName()) != null) {
            bindingResult.addError(new ObjectError("dict.name", "There is already a LuceneDictionary set up for this configuration with this name."));
        }
        this.customValidation(configName, dict, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, dict);
            return "config_dicts/create";
        }
        uiModel.asMap().clear();
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        dict.setConfig(config);
        dict.persist();
        config.getDictionaries().add(dict);
        config.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configName, httpServletRequest) + "/dicts/" + encodeUrlPathSegment(dict.getName(), httpServletRequest);
    }

    // default GET indiviual dict level
    @RequestMapping(value="/{configType}_configs/{configName}/dicts/{dictName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("dictName") String dictName, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        Dictionary dict = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getDictionaryForName(dictName);
        uiModel.addAttribute("dict", dict);
        uiModel.addAttribute("itemId", dict.getName());
        uiModel.addAttribute("configName", configName);
        uiModel.addAttribute("transformers", dict.getConfig().getTransformers());
        return "config_dicts/show";
    }

    @RequestMapping(value="/{configType}_configs/{configName}/dicts", produces = "text/html")
    public String list(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        List<Dictionary> dicts = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getDictionaries();
        if (page != null || size != null) {
            int sizeNo = Math.min(size == null ? 10 : size.intValue(), dicts.size());
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("dicts", dicts.subList(firstResult, sizeNo));
            float nrOfPages = (float) dicts.size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("dicts", dicts);
        }
        uiModel.addAttribute("configName", configName);
        return "config_dicts/list";
    }

    @RequestMapping(value="/{configType}_configs/{configName}/dicts", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Dictionary dict, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(configName, dict, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, dict);
            return String.format("%s_config_dicts/update", configType);
        }
        uiModel.asMap().clear();
        dict.setConfig(Configuration.findConfigurationsByNameEquals(configName).getSingleResult());
        dict.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configName, httpServletRequest) + "/dicts/" + encodeUrlPathSegment(dict.getName(), httpServletRequest);
    }

    @RequestMapping(value="/{configType}_configs/{configName}/dicts/{dictName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("dictName") String dictName, Model uiModel) {
        Dictionary dict = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getDictionaryForName(dictName);
        populateEditForm(uiModel, configType, configName, dict);
        return "config_dicts/update";
    }

    @RequestMapping(value="/{configType}_configs/{configName}/dicts/{dictName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("dictName") String dictName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.removeDictionary(dictName);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return String.format("redirect:/%s_configs/%s/dicts/", configType, configName.toString());
    }

    // DELETE by id
    @RequestMapping(value="/{configType}_configs/{configName}/dicts/delete-by-id/{id}", method = RequestMethod.GET, produces = "text/html")
    public String deleteById(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("id") long id, Model uiModel) {
        Dictionary toDelete = Dictionary.findDictionary(id);
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.getDictionaries().remove(toDelete);
        config.merge();
        return "redirect:/{configType}_configs/" + configName.toString() + "/dicts/";
    }

    void populateEditForm(Model uiModel, String configType, String configName, Dictionary dict) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        uiModel.addAttribute("dict", dict);
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

    public void customValidation (String configName, Dictionary dict, BindingResult br) {
        if (!FieldValidator.validSlug(dict.getName())) {
            br.addError(new ObjectError("dict.name", "The name can only contain ASCII letters and/or '-' and/or '_'"));
        }
    }

}
