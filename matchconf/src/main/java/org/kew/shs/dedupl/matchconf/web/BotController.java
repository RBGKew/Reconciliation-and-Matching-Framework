package org.kew.shs.dedupl.matchconf.web;
import org.kew.shs.dedupl.matchconf.Bot;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/bots")
@Controller
@RooWebScaffold(path = "bots", formBackingObject = Bot.class)
public class BotController {
}
