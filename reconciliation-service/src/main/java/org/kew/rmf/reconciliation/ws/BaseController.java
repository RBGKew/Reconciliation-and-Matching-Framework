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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kew.rmf.reconciliation.service.ReconciliationService;
import org.kew.web.model.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;

@Controller
public class BaseController {
	private static Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	private ReconciliationService reconciliationService;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

	public void menuAndBreadcrumbs(String activeHref, Model model) {
		MenuItem menu = makeMenu(activeHref);
		List<MenuItem> breadcrumbs = menu.toBreadcrumbTrail();
		model.addAttribute("menu", menu);
		model.addAttribute("breadcrumbs", breadcrumbs);
	}

	public void menuAndBreadcrumbs(String activeHref, ModelAndView mav) {
		MenuItem menu = makeMenu(activeHref);
		List<MenuItem> breadcrumbs = menu.toBreadcrumbTrail();
		mav.addObject("menu", menu);
		mav.addObject("breadcrumbs", breadcrumbs);
	}

	/**
	 * Generates the website menu with the active trail set accordingly.
	 */
	private MenuItem makeMenu(String activeHref) {
		MenuItem topMenu = new MenuItem("http://www.kew.org/science-conservation/research-data/resources", "Resources and databases");

		MenuItem mainMenu = new MenuItem("/#", "Reconciliation Service");

		mainMenu.add(new MenuItem("/", "Introduction"));

		MenuItem aboutMenu = new MenuItem("/about", "Available configurations");
		if (reconciliationService.getMatchers() != null) {
			for (String matcher : reconciliationService.getMatchers().keySet()) {
				aboutMenu.add(
						new MenuItem("/about/" + matcher, matcher)
						);
			}
		}
		mainMenu.add(aboutMenu);

		MenuItem helpMenu = new MenuItem("/help", "Usage");
		helpMenu.add(new MenuItem("/help#installation", "Overview and installation"));
		helpMenu.add(new MenuItem("/help#data-preparation", "Data preparation"));
		helpMenu.add(new MenuItem("/help#reconciling", "Querying the Reconciliation Service"));
		helpMenu.add(new MenuItem("/help#extending", "Extending data using MQL"));
		helpMenu.add(new MenuItem("/help#exporting", "Exporting data"));
		helpMenu.add(new MenuItem("/help#troubleshooting", "Troubleshooting"));
		helpMenu.add(new MenuItem("/help#advanced", "Advanced data manipulation"));
		helpMenu.add(new MenuItem("/help#sourcecode", "Source code"));

		mainMenu.add(helpMenu);

		topMenu.add(mainMenu);

		topMenu.setActiveTrail(activeHref);

		return topMenu;
	}

	/**
	 * Error handler for things not caught by the Exception Resolver.
	 */
	@RequestMapping("/error")
	public String customError(HttpServletRequest request, HttpServletResponse response, Model model) {
		// retrieve some useful information from the request
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) statusCode = 500;
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String exceptionMessage = getExceptionMessage(throwable, statusCode);

		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}

		logger.warn("Web error {} {}: {}", statusCode, requestUri, exceptionMessage);

		model.addAttribute("url", requestUri);
		model.addAttribute("datetime", simpleDateFormat.format(new Date()));
		model.addAttribute("contactemail", "apps-support@kew.org");
		model.addAttribute("statusCode", statusCode);
		model.addAttribute("statusMessage", HttpStatus.valueOf(statusCode).getReasonPhrase());
		menuAndBreadcrumbs("/#", model);

		if (statusCode == 404) {
			return "404";
		}
		else {
			return "500";
		}
	}

	private String getExceptionMessage(Throwable throwable, Integer statusCode) {
		if (throwable != null) {
			return Throwables.getRootCause(throwable).getMessage();
		}
		HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
		return httpStatus.getReasonPhrase();
	}
}
