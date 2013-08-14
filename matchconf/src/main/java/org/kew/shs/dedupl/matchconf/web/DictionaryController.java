package org.kew.shs.dedupl.matchconf.web;
import org.kew.shs.dedupl.matchconf.Dictionary;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/dictionarys")
@Controller
@RooWebScaffold(path = "dictionarys", formBackingObject = Dictionary.class)
public class DictionaryController {
}
