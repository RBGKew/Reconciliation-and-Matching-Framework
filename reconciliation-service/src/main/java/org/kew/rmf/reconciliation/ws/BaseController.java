package org.kew.rmf.reconciliation.ws;

import java.util.List;

import org.kew.rmf.reconciliation.service.ReconciliationService;
import org.kew.web.model.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class BaseController {

	@Autowired
	private ReconciliationService reconciliationService;

	public void menuAndBreadcrumbs(String activeHref, Model model) {
		MenuItem menu = makeMenu(activeHref);
		List<MenuItem> breadcrumbs = menu.toBreadcrumbTrail();
		model.addAttribute("menu", menu);
		model.addAttribute("breadcrumbs", breadcrumbs);
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
}
