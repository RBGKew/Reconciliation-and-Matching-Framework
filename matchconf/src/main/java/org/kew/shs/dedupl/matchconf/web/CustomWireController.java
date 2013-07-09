package org.kew.shs.dedupl.matchconf.web;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.Wire;
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
public class CustomWireController {

    @RequestMapping(value="/{configType}_configs/{configName}/wires", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model uiModel) {
        populateEditForm(uiModel, configName, new Wire());
        return String.format("%s_config_wires/create", configType);
    }

    // Default Post
    @RequestMapping(value="/{configType}_configs/{configName}/wires", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Wire wire, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        // assert unique_together:config&name
        if (Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wire.getName()) != null) {
            bindingResult.addError(new ObjectError("wire.name", "There is already a Wire set up for this configuration with this name."));
        }
        this.customValidation(configName, wire, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configName, wire);
            return String.format("%s_config_wires/create", configType);
        }
        uiModel.asMap().clear();
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        wire.setConfiguration(config);
        wire.persist();
        config.getWiring().add(wire);
        config.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configName, httpServletRequest) + "/wires/" + encodeUrlPathSegment(wire.getName(), httpServletRequest);
    }

    // default GET indiviual wire level
    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, Model uiModel) {
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        uiModel.addAttribute("wire", wire);
        uiModel.addAttribute("itemId", wire.getName());
        uiModel.addAttribute("configName", configName);
        uiModel.addAttribute("transformers", wire.getConfiguration().getTransformers());
        return String.format("%s_config_wires/show", configType);
    }

    @RequestMapping(value="/{configType}_configs/{configName}/wires", produces = "text/html")
    public String list(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        List<Wire> wires = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWiring();
        if (page != null || size != null) {
            int sizeNo = Math.min(size == null ? 10 : size.intValue(), wires.size());
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("wires", wires.subList(firstResult, sizeNo));
            float nrOfPages = (float) wires.size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("wires", wires);
        }
        uiModel.addAttribute("configName", configName);
        return String.format("%s_config_wires/list", ConfigSwitch.getTypeForUrl(configName));
    }

    @RequestMapping(value="/{configType}_configs/{configName}/wires", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Wire wire, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(configName, wire, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configName, wire);
            return String.format("%s_config_wires/update", configType);
        }
        uiModel.asMap().clear();
        wire.setConfiguration(Configuration.findConfigurationsByNameEquals(configName).getSingleResult());
        wire.merge();
        return String.format("redirect:/%s_configs/", configType) + encodeUrlPathSegment(configName, httpServletRequest) + "/wires/" + encodeUrlPathSegment(wire.getName(), httpServletRequest);
    }

    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, Model uiModel) {
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        populateEditForm(uiModel, configName, wire);
        return String.format("%s_config_wires/update", configType);
    }

    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.removeWire(wireName);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return String.format("redirect:/%s_configs/%s/wires/", configType, configName.toString());
    }

    void populateEditForm(Model uiModel, String configName, Wire wire) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        uiModel.addAttribute("wire", wire);
        uiModel.addAttribute("configName", configName);
        uiModel.addAttribute("matchers", config.getMatchers());
        uiModel.addAttribute("transformers", config.getTransformers());
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

    public void customValidation (String configName, Wire wire, BindingResult br) {
        if (!FieldValidator.validSlug(wire.getName())) {
            br.addError(new ObjectError("wire.name", "The name can only contain ASCII letters and/or '-' and/or '_'"));
        }
    }

}
