package org.kew.reconciliation.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.kew.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.service.resultformatter.ReconciliationResultFormatter;
import org.kew.reconciliation.service.resultformatter.ReconciliationResultPropertyFormatter;
import org.kew.stringmod.dedupl.configuration.MatchConfiguration;
import org.kew.stringmod.dedupl.configuration.ReconciliationServiceConfiguration;
import org.kew.stringmod.dedupl.exception.MatchExecutionException;
import org.kew.stringmod.dedupl.exception.TooManyMatchesException;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

/**
 * The ReconciliationService handles loading and using multiple reconciliation configurations.
 */
@Service
public class ReconciliationService {
	private static Logger logger = LoggerFactory.getLogger(ReconciliationService.class);

	@Value("#{'${configurations}'.split(',')}")
	private List<String> initialConfigurations;

	@Autowired
	private TaskExecutor taskExecutor;

	private final List<String> loadedConfigurationFilenames = new ArrayList<String>();

	private final String CONFIG_BASE = "/META-INF/spring/reconciliation-service/";
	private final String CONFIG_EXTENSION = ".xml";
	private final String ENV = System.getProperty("environment", "unknown");

	private final Map<String, ConfigurableApplicationContext> contexts = new HashMap<String, ConfigurableApplicationContext>();
	private final Map<String, LuceneMatcher> matchers = new HashMap<String, LuceneMatcher>();
	private final Map<String, Integer> totals = new HashMap<String, Integer>();


	/**
	 * Kicks off tasks (threads) to load the initial configurations.
	 */
	@PostConstruct
	public void init() {
		logger.debug("Initialising reconciliation service");

		// Load up the matchers from the specified files
		if (initialConfigurations != null) {
			for (String config : initialConfigurations) {
				taskExecutor.execute(new BackgroundConfigurationLoaderTask(config + CONFIG_EXTENSION));
			}
		}
	}

	/**
	 * For loading a configuration in the background (i.e. in a thread).
	 */
	private class BackgroundConfigurationLoaderTask implements Runnable {
		private String configFileName;

		public BackgroundConfigurationLoaderTask(String configFileName) {
			this.configFileName = configFileName;
		}

		@Override
		public void run() {
			try {
				loadConfiguration(configFileName);
			}
			catch (ReconciliationServiceException e) {
				logger.error(configFileName + ": Error while loading", e);
			}
		}
	}

	/**
	 * Lists the available configuration files from the classpath.
	 */
	public List<String> listAvailableConfigurationFiles() throws ReconciliationServiceException {
		List<String> availableConfigurations = new ArrayList<>();
		ResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
		try {
			Resource[] configurationResources = pmrpr.getResources("classpath*:"+CONFIG_BASE+"*Match.xml");
			logger.debug("Found {} configuration file resources", configurationResources.length);

			for (Resource resource : configurationResources) {
				availableConfigurations.add(resource.getFilename());
			}
		}
		catch (IOException e) {
			throw new ReconciliationServiceException("Unable to list available configurations", e);
		}

		return availableConfigurations;
	}

	/**
	 * Loads a single configuration.
	 */
	public void loadConfiguration(String configFileName) throws ReconciliationServiceException {
		synchronized (loadedConfigurationFilenames) {
			if (loadedConfigurationFilenames.contains(configFileName)) {
				throw new ReconciliationServiceException("Match configuration "+configFileName+" is already loaded.");
			}
			else {
				loadedConfigurationFilenames.add(configFileName);
			}
		}

		String configurationFile = CONFIG_BASE + configFileName;
		logger.info("{}: Loading configuration from file", configFileName, configurationFile);

		ConfigurableApplicationContext context = new GenericXmlApplicationContext(configurationFile);
		context.registerShutdownHook();

		LuceneMatcher matcher = context.getBean("engine", LuceneMatcher.class);
		String configName = matcher.getConfig().getName();

		contexts.put(configFileName, context);
		matchers.put(configName, matcher);

		try {
			matcher.loadData();
			totals.put(configName, matcher.getIndexReader().numDocs());
			logger.debug("{}: Loaded data", configName);

			// Append " (environment)" to Metadata name, to help with interactive testing
			Metadata metadata = getMetadata(configName);
			if (metadata != null) {
				if (!"prod".equals(ENV)) {
					metadata.setName(metadata.getName() + " (" + ENV + ")");
				}
			}
		}
		catch (Exception e) {
			logger.error("Problem loading configuration "+configFileName, e);

			context.close();
			totals.remove(configName);
			matchers.remove(configName);
			contexts.remove(configFileName);

			synchronized (loadedConfigurationFilenames) {
				loadedConfigurationFilenames.remove(configFileName);
			}

			throw new ReconciliationServiceException("Problem loading configuration "+configFileName, e);
		}
	}

	/**
	 * Unloads a single configuration.
	 */
	public void unloadConfiguration(String configFileName) throws ReconciliationServiceException {
		synchronized (loadedConfigurationFilenames) {
			if (!loadedConfigurationFilenames.contains(configFileName)) {
				throw new ReconciliationServiceException("Match configuration "+configFileName+" is not loaded.");
			}

			logger.info("{}: Unloading configuration", configFileName);

			ConfigurableApplicationContext context = contexts.get(configFileName);

			String configName = configFileName.substring(0, configFileName.length() - 4);
			totals.remove(configName);
			matchers.remove(configName);
			contexts.remove(configName);

			context.close();

			loadedConfigurationFilenames.remove(configFileName);
		}
	}

	/**
	 * Retrieve reconciliation service metadata.
	 * @throws MatchExecutionException if the requested matcher doesn't exist.
	 */
	public Metadata getMetadata(String configName) throws MatchExecutionException {
		ReconciliationServiceConfiguration reconcilationConfig = getReconciliationServiceConfiguration(configName);
		if (reconcilationConfig != null) {
			Metadata metadata = reconcilationConfig.getReconciliationServiceMetadata();
			if (metadata.getDefaultTypes() == null || metadata.getDefaultTypes().length == 0) {
				throw new MatchExecutionException("No default type specified, Open Refine 2.6 would fail");
			}
			return metadata;
		}
		return null;
	}

	/**
	 * Convert single query string into query properties.
	 * @throws MatchExecutionException if the requested matcher doesn't exist.
	 */
	public QueryStringToPropertiesExtractor getPropertiesExtractor(String configName) throws MatchExecutionException {
		ReconciliationServiceConfiguration reconcilationConfig = getReconciliationServiceConfiguration(configName);
		if (reconcilationConfig != null) {
			return reconcilationConfig.getQueryStringToPropertiesExtractor();
		}
		return null;
	}

	/**
	 * Formatter to convert result into single string.
	 * @throws MatchExecutionException if the requested matcher doesn't exist.
	 */
	public ReconciliationResultFormatter getReconciliationResultFormatter(String configName) throws MatchExecutionException {
		ReconciliationServiceConfiguration reconcilationConfig = getReconciliationServiceConfiguration(configName);
		if (reconcilationConfig != null) {
			ReconciliationResultFormatter reconciliationResultFormatter = reconcilationConfig.getReconciliationResultFormatter();
			if (reconciliationResultFormatter != null) {
				return reconciliationResultFormatter;
			}
			else {
				// Set it to the default one
				ReconciliationResultPropertyFormatter formatter = new ReconciliationResultPropertyFormatter(reconcilationConfig);
				reconcilationConfig.setReconciliationResultFormatter(formatter);
				return formatter;
			}
		}
		return null;
	}

	/**
	 * Perform match query against specified configuration.
	 * @throws MatchExecutionException 
	 * @throws TooManyMatchesException 
	 */
	public synchronized List<Map<String,String>> doQuery(String configName, Map<String, String> userSuppliedRecord) throws TooManyMatchesException, MatchExecutionException {
		List<Map<String,String>> matches = null;

		LuceneMatcher matcher = getMatcher(configName);

		if (matcher == null) {
			// When no matcher specified with that configuration
			logger.warn("Invalid match configuration «{}» requested", configName);
			return null;
		}

		matches = matcher.getMatches(userSuppliedRecord, 5);
		// Just write out some matches to std out:
		logger.debug("Found some matches: {}", matches.size());
		if (matches.size() < 4) {
			logger.debug("Matches for {} are {}", userSuppliedRecord, matches);
		}

		return matches;
	}

	/**
	 * Retrieve reconciliation service configuration.
	 * @throws MatchExecutionException if the requested configuration doesn't exist.
	 */
	public ReconciliationServiceConfiguration getReconciliationServiceConfiguration(String configName) throws MatchExecutionException {
		MatchConfiguration matchConfig = getMatcher(configName).getConfig();
		if (matchConfig instanceof ReconciliationServiceConfiguration) {
			ReconciliationServiceConfiguration reconcilationConfig = (ReconciliationServiceConfiguration) matchConfig;
			return reconcilationConfig;
		}
		return null;
	}

	/* • Getters and setters • */
	public Map<String, LuceneMatcher> getMatchers() {
		return matchers;
	}
	public LuceneMatcher getMatcher(String matcher) throws MatchExecutionException {
		if (matchers.get(matcher) == null) {
			throw new MatchExecutionException("No matcher called '"+matcher+"' exists.");
		}
		return matchers.get(matcher);
	}

	public List<String> getInitialConfigurations() {
		return initialConfigurations;
	}
	public void setInitialConfigurations(List<String> initialConfigurations) {
		this.initialConfigurations = initialConfigurations;
	}

	public List<String> getLoadedConfigurationFilenames() {
		return loadedConfigurationFilenames;
	}

	public Map<String, Integer> getTotals() {
		return totals;
	}
}
