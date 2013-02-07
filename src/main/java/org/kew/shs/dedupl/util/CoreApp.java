package org.kew.shs.dedupl.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.kew.shs.dedupl.DataHandler;

import net.sf.ehcache.CacheManager;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CoreApp {

	protected static ClassPathXmlApplicationContext getContext(String[] args, String generalConfig) throws ParseException{

		// Some arguments possibly want to be picked up from the command-line
		CommandLine line = getParsedCommandLine(args);

		/* first define the location of the spring context configuration that contains
		 * the design of the dedup/matching process; this can be overwritten by
		 * command-line args
		 */
		String dataDir = ".";
		if (line.hasOption("d")) {
			dataDir = line.getOptionValue("d");
		}
		System.setProperty("NMT_DATADIR", dataDir);
		String dedupDesign = "file:" + dataDir + "config.xml";


		// This command picks up the Spring config file from the classpath
		// (the dedup-design config may have a "file:" prefix meaning it's an absolute file system path)
		return new ClassPathXmlApplicationContext(new String[] {dedupDesign, generalConfig});

	}

	protected static CommandLine getParsedCommandLine(String[] args) throws ParseException {
		CommandLineParser argParser = new GnuParser();
		final Options options = new Options();
		options.addOption("d", "data-dir", true,
				"location of the directory containing the config and the input and output files");
		CommandLine line = argParser.parse( options, args );
		return line;
	}

	protected static void runEngineAndCache (ConfigurableApplicationContext context) {
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
