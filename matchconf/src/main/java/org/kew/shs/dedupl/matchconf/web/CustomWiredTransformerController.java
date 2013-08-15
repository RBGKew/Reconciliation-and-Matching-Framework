package org.kew.shs.dedupl.matchconf.web;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.Wire;
import org.kew.shs.dedupl.matchconf.WiredTransformer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@Controller
public class CustomWiredTransformerController {

    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}/wired_transformers", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @Valid WiredTransformer wiredTransformer, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, wiredTransformer);
            return "wiredtransformers/create";
        }
        uiModel.asMap().clear();
        wiredTransformer.persist();
        return "redirect:/wiredtransformers/" + encodeUrlPathSegment(wiredTransformer.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}/wired_transformers", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, Model uiModel) {
        WiredTransformer instance = new WiredTransformer();
        populateEditForm(uiModel, configType, configName, instance);
        return "wired_transformers/create";
    }

    @RequestMapping(value = "/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers/{wiredTransformerName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @PathVariable("wiredTransformerName") String wiredTransformerName, Model uiModel) {
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        uiModel.addAttribute("sourceTransformer", wire.getSourceTransformerForName(wiredTransformerName));
        uiModel.addAttribute("lookupTransformer", wire.getSourceTransformerForName(wiredTransformerName));
        uiModel.addAttribute("transformerType", transformerType);
        uiModel.addAttribute("itemId", wiredTransformerName);
        return "wired_transformers/show";
    }

    void populateEditForm(Model uiModel, String configType, String configName, WiredTransformer wTransformer) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        uiModel.addAttribute("wiredTransformer", wTransformer);
        uiModel.addAttribute("transformers", Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getTransformers());
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

}
