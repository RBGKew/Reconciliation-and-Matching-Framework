package org.kew.shs.dedupl.matchconf.web;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

@RequestMapping("/dictionaries")
@Controller
public class DictionaryController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Dictionary());
        return "dictionaries/create";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("dictionaries", Dictionary.findDictionaryEntries(firstResult, sizeNo));
            float nrOfPages = (float) Dictionary.countDictionarys() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("dictionaries", Dictionary.findAllDictionarys());
        }
        return "dictionaries/list";
    }

    void populateEditForm(Model uiModel, Dictionary dictionary) {
        uiModel.addAttribute("dictionary", dictionary);
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

    @RequestMapping(value = "/{name}", produces = "text/html")
    public String show(@PathVariable("name") String name, Model uiModel) {
        uiModel.addAttribute("dictionary", Dictionary.findDictionariesByNameEquals(name).getSingleResult());
        uiModel.addAttribute("itemId", name);
        return "dictionaries/show";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Dictionary dictionary, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(dictionary, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dictionary);
            return "dictionaries/create";
        }
        uiModel.asMap().clear();
        dictionary.persist();
        return "redirect:/dictionaries/" + encodeUrlPathSegment(dictionary.getName(), httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Dictionary dictionary, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(dictionary, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dictionary);
            return "dictionaries/update";
        }
        uiModel.asMap().clear();
        dictionary.merge();
        return "redirect:/dictionaries/" + encodeUrlPathSegment(dictionary.getName(), httpServletRequest);
    }

    @RequestMapping(value = "/{name}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("name") String name, Model uiModel) {
        populateEditForm(uiModel, Dictionary.findDictionariesByNameEquals(name).getSingleResult());
        return "dictionaries/update";
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("name") String name, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Dictionary dictionary = Dictionary.findDictionariesByNameEquals(name).getSingleResult();
        dictionary.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/dictionaries";
    }
    public void customValidation (Dictionary dict, BindingResult br) {
        if (!FieldValidator.validSlug(dict.getName())) {
            br.addError(new ObjectError("dict.name", "The name has to be set and can only contain ASCII letters and/or '-' and/or '_'"));
        }
    }

}
