package org.kew.shs.dedupl.util;

import java.io.File;

import net.sf.ehcache.CacheManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.kew.shs.dedupl.DataHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class CoreApp {

	public static void main(String[] args) throws Exception {

		// Some arguments possibly want to be picked up from the command-line
		CommandLine line = getParsedCommandLine(args);

		// where is the work directory that contains the design configuration as well
		// as the data files?
		String workDir = ".";
		if (line.hasOption("d")) workDir = line.getOptionValue("d").trim();
		// the name of the core configuration file that contains the dedup/match design
		// NOTE this is relative to the working directory
		String configFileName = "config.xml";
		if (line.hasOption("c")) configFileName = line.getOptionValue("c").trim();
		String dedupDesign = "file:" + new File(new File(workDir), configFileName).toPath();

		runEngineAndCache(new GenericXmlApplicationContext(dedupDesign));

	}

	protected static CommandLine getParsedCommandLine(String[] args) throws ParseException {
		CommandLineParser argParser = new GnuParser();
		final Options options = new Options();
		options.addOption("d", "data-dir", true,
				"location of the directory containing the config and the input and output files");
		options.addOption("c", "config-file", true,
				"name of the config file (default: 'config.xml')");
		CommandLine line = argParser.parse( options, args );
		return line;
	}

	protected static void runEngineAndCache (ConfigurableApplicationContext context) throws Exception {
		/*
		* Get the "engine" bean from the Spring config - Spring has instantiated it
		* with the values set in the application-context file, which defaults to
		* deduplication mode. If the context provided is a matching one, the bean
		* definition will be changed prior to its initialisation.
		* TODO: this is horrible, don't know of a better way of doing it.
		*/
		DataHandler engine = (DataHandler) context.getBean("engine");
		// Call the run method
		engine.run();
		// Dump the cache statistics
		CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
		String[] cacheNames = cacheManager.getCacheNames();
		for (int i = 0; i < cacheNames.length; i++) {
			String cacheName = cacheNames[i];
			System.out.println(cacheName+" - "+ cacheManager.getCache(cacheName).getStatistics().toString());
		}
	}

}
