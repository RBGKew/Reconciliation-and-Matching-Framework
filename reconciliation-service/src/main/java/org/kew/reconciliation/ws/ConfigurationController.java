package org.kew.reconciliation.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.kew.reconciliation.service.ReconciliationService;
import org.kew.reconciliation.service.ReconciliationService.ConfigurationStatus;
import org.kew.reconciliation.service.ReconciliationServiceException;
import org.kew.rmf.transformers.Transformer;
import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.configuration.ReconciliationServiceConfiguration;
import org.kew.stringmod.dedupl.exception.MatchExecutionException;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Information, administration, help pages etc.
 */
@Controller
public class ConfigurationController {
	private static Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	@Autowired
	private ReconciliationService reconciliationService;

	@RequestMapping(produces="text/html", value={"/","/about"}, method = RequestMethod.GET)
	public String doWelcome(Model model) {
		model.addAttribute("availableMatchers", reconciliationService.getMatchers().keySet());
		return "about-general";
	}

	@RequestMapping(produces="text/html", value = "/admin", method = RequestMethod.GET)
	public String doConfigurationAdmin(Model model) throws ReconciliationServiceException {
		logger.info("Request for configuration admin page");

		Map<String,ConfigurationStatus> configurationStatuses = reconciliationService.getConfigurationStatuses();
		logger.debug("Loaded/loading config files: {}", configurationStatuses);

		Map<String,ConfigurationStatus> configurations = new TreeMap<>();

		for (String configurationFileName : reconciliationService.listAvailableConfigurationFiles()) {
			configurations.put(configurationFileName, configurationStatuses.get(configurationFileName) == null ? ConfigurationStatus.NOT_LOADED : configurationStatuses.get(configurationFileName));
		}

		model.addAttribute("configurations", configurations);

		return "configuration-admin";
	}

	@RequestMapping(produces="text/html", value = "/admin", method = RequestMethod.POST)
	public String doConfigurationAdmin(
			HttpServletRequest request,
			@RequestParam(required=false) String load,
			@RequestParam(required=false) String reload,
			@RequestParam(required=false) String unload,
			Model model) throws JsonGenerationException, JsonMappingException, IOException, ReconciliationServiceException {
		logger.info("Request for configuration loading L:{} R:{} U:{}", load, reload, unload);

		if (reload != null) {
			// TODO ••• Check user input! •••
			String configName = reload;
			logger.info("Request to reload {}", configName);

			reconciliationService.unloadConfiguration(configName);
			reconciliationService.loadConfigurationInBackground(configName);
		}

		if (unload != null) {
			// TODO ••• Check user input! •••
			String configName = unload;
			logger.info("Request to unload {}", configName);

			reconciliationService.unloadConfiguration(configName);
		}

		if (load != null) {
			// TODO ••• Check user input! •••
			String configName = load;
			logger.info("Request to load {}", configName);

			reconciliationService.loadConfigurationInBackground(configName);
		}

		return "redirect:/admin";
	}

	@RequestMapping(produces="text/html", value = "/about/{configName}", method = RequestMethod.GET)
	public String doAbout(@PathVariable String configName, Model model) {
		List<String> properties = new ArrayList<String>();
		Map<String,String> p_matchers = new HashMap<String,String>();
		Map<String,List<String>> p_transformers = new HashMap<String,List<String>>();

		LuceneMatcher matcher;
		ReconciliationServiceConfiguration configuration;
		try {
			matcher = reconciliationService.getMatcher(configName);
			configuration = reconciliationService.getReconciliationServiceConfiguration(configName);
		}
		catch (MatchExecutionException e) {
			// TODO: Make a 404.
			return "about-matcher";
		}

		if (matcher != null){
			model.addAttribute("reconciliationConfiguration", configuration);
			for (Property p : matcher.getConfig().getProperties()){
				properties.add(p.getQueryColumnName());
				p_matchers.put(p.getQueryColumnName(), p.getMatcher().getClass().getCanonicalName());
				List<String> p_t = new ArrayList<String>();
				for (Transformer t : p.getQueryTransformers()){
					p_t.add(t.getClass().getCanonicalName());
				}
				p_transformers.put(p.getQueryColumnName(), p_t);
			}
			model.addAttribute("total", reconciliationService.getTotals().get(configName));
			model.addAttribute("configName", configName);
			model.addAttribute("properties", properties);
			model.addAttribute("matchers", p_matchers);
			model.addAttribute("transformers", p_transformers);
		}
		return "about-matcher";
	}

	@RequestMapping(produces="text/html", value={"/help"}, method = RequestMethod.GET)
	public String doHelp(Model model) {
		return "help";
	}
}
