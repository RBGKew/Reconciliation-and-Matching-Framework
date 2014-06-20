package org.kew.reconciliation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.stringmod.dedupl.configuration.MatchConfiguration;
import org.kew.stringmod.dedupl.configuration.ReconciliationServiceConfiguration;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * The ReconciliationService handles loading and using multiple reconciliation configurations.
 */
@Service
public class ReconciliationService {
	private static Logger logger = LoggerFactory.getLogger(ReconciliationService.class);

	@Value("#{'${configFiles}'.split(',')}")
	private List<String> configFiles;

	private Map<String, LuceneMatcher> matchers = new HashMap<String, LuceneMatcher>();
	private Map<String, Integer> totals = new HashMap<String, Integer>();

	private boolean initialised = false;

	/**
	 * Loads the configured match configurations.
	 */
	@PostConstruct
	public void init() {
		logger.debug("Initialising reconciliation service");
		if (!initialised) {
			// Load up the matchers from the specified files
			if (configFiles != null) {
				for (String configFile : configFiles) {
					logger.debug("Processing configFile {}", configFile);
					@SuppressWarnings("resource")
					ConfigurableApplicationContext context = new GenericXmlApplicationContext(configFile);
					context.registerShutdownHook();
					LuceneMatcher matcher = (LuceneMatcher) context.getBean("engine");
					try {
						matcher.loadData(); 
						logger.debug("Loaded data for configFile {}", configFile);
						String configName = matcher.getConfig().getName(); 
						matchers.put(configName, matcher);
						totals.put(configName, matcher.getIndexReader().numDocs());
						logger.debug("Stored matcher from configFile {} with name {}", configFile, configName);
					}
					catch (Exception e) {
						logger.error("Problem initialising handler from configFile " + configFile, e);
					}
				}
			}
			initialised = true;
		}
		else {
			logger.warn("Reconciliation service was already initialised");
		}
	}

	/**
	 * Retrieve reconciliation service metadata.
	 */
	public Metadata getMetadata(String configName) {
		MatchConfiguration matchConfig = getMatcher(configName).getConfig();
		if (matchConfig instanceof ReconciliationServiceConfiguration) {
			ReconciliationServiceConfiguration reconcilationConfig = (ReconciliationServiceConfiguration) matchConfig;
			return reconcilationConfig.getReconciliationServiceMetadata();
		}
		return null;
	}

	/* • Getters and setters • */
	public Map<String, LuceneMatcher> getMatchers() {
		return matchers;
	}
	public LuceneMatcher getMatcher(String matcher) {
		return matchers.get(matcher);
	}

	public List<String> getConfigFiles() {
		return configFiles;
	}
	public void setConfigFiles(List<String> configFiles) {
		this.configFiles = configFiles;
	}
}
