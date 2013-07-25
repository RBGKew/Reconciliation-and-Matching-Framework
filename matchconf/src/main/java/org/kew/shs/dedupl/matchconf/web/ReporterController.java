package org.kew.shs.dedupl.matchconf.web;
import org.kew.shs.dedupl.matchconf.Reporter;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/reporters")
@Controller
@RooWebScaffold(path = "reporters", formBackingObject = Reporter.class)
public class ReporterController {
}
