package org.kew.shs.dedupl.matchconf.web;
import org.kew.shs.dedupl.matchconf.Matcher;
import org.kew.shs.dedupl.matchconf.Wire;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/matchers")
@Controller
@RooWebScaffold(path = "matchers", formBackingObject = Matcher.class)
public class MatcherController {

    void populateEditForm(Model uiModel, Matcher matcher) {
        uiModel.addAttribute("matcher", matcher);
        uiModel.addAttribute("matchers", Matcher.findAllMatchers());
        uiModel.addAttribute("wires", Wire.findAllWires());
    }
}
