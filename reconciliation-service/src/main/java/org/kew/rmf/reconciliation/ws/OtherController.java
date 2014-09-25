package org.kew.rmf.reconciliation.ws;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Information, help pages etc.
 */
@Controller
public class OtherController {
	//private static Logger logger = LoggerFactory.getLogger(OtherController.class);

	@RequestMapping(produces="text/html", value = "/", method = RequestMethod.GET)
	public String doWelcome(Model model) {
		return "index";
	}

	@RequestMapping(produces="text/html", value = "/help", method = RequestMethod.GET)
	public String doHelp(Model model) {
		return "help";
	}
}
