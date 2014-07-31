package org.kew.stringmod.matchconf.web;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.stringmod.matchconf.Configuration;
import org.kew.stringmod.matchconf.Wire;
import org.kew.stringmod.matchconf.WiredTransformer;
import org.kew.stringmod.matchconf.utils.GetterSetter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@Controller
public class CustomWiredTransformerController {

    @SuppressWarnings("serial")
    private final static HashSet<String> T_TYPES = new HashSet<String>() {{add("query"); add("authority");}};

    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @Valid WiredTransformer wiredTransformer, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws Exception {
        if (!T_TYPES.contains(transformerType)) throw new Exception(String.format("The transformer type has to be of %s", T_TYPES));
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, wiredTransformer);
            return "wiredtransformers/create";
        }
        uiModel.asMap().clear();
        wiredTransformer.persist();
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        new GetterSetter<List<WiredTransformer>>().getattr(wire, transformerType + "Transformers").add(wiredTransformer);
        wire.merge();
        return String.format("redirect:/%s_configs/%s/wires/%s/%s_transformers/%s", configType, configName, wireName, transformerType, wiredTransformer.getName());
    }

    @RequestMapping(value="/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, Model uiModel) throws Exception {
        if (!T_TYPES.contains(transformerType)) throw new Exception(String.format("The transformer type has to be of %s", T_TYPES));
        WiredTransformer instance = new WiredTransformer();
        populateEditForm(uiModel, configType, configName, instance);
        return "wired_transformers/create";
    }

    @RequestMapping(value = "/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers/{wiredTransformerName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @PathVariable("wiredTransformerName") String wiredTransformerName, Model uiModel) throws Exception {
        if (!T_TYPES.contains(transformerType)) throw new Exception(String.format("The transformer type has to be of %s", T_TYPES));
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        WiredTransformer wTransformer = wire.getWiredTransformer(transformerType, wiredTransformerName);
        uiModel.addAttribute("wiredTransformer", wTransformer);
        uiModel.addAttribute("transformerType", transformerType);
        uiModel.addAttribute("itemId", wiredTransformerName);
        return "wired_transformers/show";
    }
    @RequestMapping(value = "/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers", produces = "text/html")
    public String list(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) throws Exception {
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        List<WiredTransformer> wTransformers = new GetterSetter<List<WiredTransformer>>().getattr(wire, transformerType + "Transformers");
        uiModel.addAttribute("transformerType", transformerType);
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("wiredTransformers", wTransformers.subList(firstResult, Math.min(firstResult + sizeNo, wTransformers.size())));
            float nrOfPages = (float) wTransformers.size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("wiredTransformers", wTransformers);
        }
        return "wired_transformers/list";
    }

    @RequestMapping(value = "/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @Valid WiredTransformer wiredTransformer, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, wiredTransformer);
            return "wired_transformers/update";
        }
        uiModel.asMap().clear();
        wiredTransformer.merge();
        return String.format("redirect:/%s_configs/%s/wires/%s/%s_transformers/%s", configType, configName, wireName, transformerType, wiredTransformer.getName());
    }

    @RequestMapping(value = "/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers/{wiredTransformerName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @PathVariable("wiredTransformerName") String wiredTransformerName, Model uiModel) throws Exception {
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        populateEditForm(uiModel, configType, configName, wire.getWiredTransformer(transformerType, wiredTransformerName));
        return "wired_transformers/update";
    }

    @RequestMapping(value = "/{configType}_configs/{configName}/wires/{wireName}/{transformerType}_transformers/{wiredTransformerName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("wireName") String wireName, @PathVariable("transformerType") String transformerType, @PathVariable("wiredTransformerName") String wiredTransformerName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) throws Exception {
        Wire wire = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getWireForName(wireName);
        WiredTransformer wiredTransformer = wire.getWiredTransformer(transformerType, wiredTransformerName);
        new GetterSetter<List<WiredTransformer>>().getattr(wire, transformerType + "Transformers").remove(wiredTransformer);
        wire.merge();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return String.format("redirect:/%s_configs/%s/wires/%s/%s_transformers", configType, configName, wireName, transformerType);
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
