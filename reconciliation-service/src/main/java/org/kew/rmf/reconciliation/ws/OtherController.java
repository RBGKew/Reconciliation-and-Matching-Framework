/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reconciliation.ws;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private BaseController baseController;

	@RequestMapping(produces="text/html", value = "/", method = RequestMethod.GET)
	public String doWelcome(Model model) {
		baseController.menuAndBreadcrumbs("/", model);
		return "index";
	}

	@RequestMapping(produces="text/html", value = "/help", method = RequestMethod.GET)
	public String doHelp(Model model) {
		baseController.menuAndBreadcrumbs("/help", model);
		return "help";
	}
}
