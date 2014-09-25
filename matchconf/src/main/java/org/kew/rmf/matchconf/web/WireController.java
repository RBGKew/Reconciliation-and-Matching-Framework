package org.kew.rmf.matchconf.web;
import org.kew.rmf.matchconf.Wire;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/wires")
@Controller
@RooWebScaffold(path = "wires", formBackingObject = Wire.class)
public class WireController {
}
