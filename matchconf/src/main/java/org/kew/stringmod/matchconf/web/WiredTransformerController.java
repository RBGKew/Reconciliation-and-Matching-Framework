package org.kew.stringmod.matchconf.web;
import org.kew.stringmod.matchconf.WiredTransformer;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/wiredtransformers")
@Controller
@RooWebScaffold(path = "wiredtransformers", formBackingObject = WiredTransformer.class)
public class WiredTransformerController {
}
