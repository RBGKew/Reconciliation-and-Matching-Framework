package org.kew.shs.dedupl.matchconf.web;
import org.kew.shs.dedupl.matchconf.Configuration;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/configurations")
@Controller
@RooWebScaffold(path = "configurations", formBackingObject = Configuration.class)
public class ConfigurationController {
}
