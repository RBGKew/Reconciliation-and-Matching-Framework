package org.kew.shs.dedupl.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CoreApp {

	protected static ClassPathXmlApplicationContext getContext(String[] args, String generalConfig) throws ParseException{

		// Some arguments possibly want to be picked up from the command-line
		CommandLine line = getParsedCommandLine(args);

		/* first define the location of the spring context configuration that contains
		 * the design of the dedup/matching process; this can be overwritten by
		 * command-line args
		 */
		String dedupConfig = "config.xml";
		if (line.hasOption("c")) {
			dedupConfig = line.getOptionValue("c");
		}

		/*
		 * Get the "engine" bean from the Spring config - Spring has instantiated it
		 * with the values set in the application-context file, which defaults to
		 * deduplication mode. If the context provided is a matching one, the bean
		 * definition will be changed prior to its initialisation.
		 * TODO: this is horrible, don't know of a better way of doing it.
		 */
		// This command picks up the Spring config file from the classpath
		return new ClassPathXmlApplicationContext(new String[] {dedupConfig, generalConfig});

	}

	protected static CommandLine getParsedCommandLine(String[] args)
			throws ParseException {
				CommandLineParser argParser = new GnuParser();
				final Options options = new Options();
				options.addOption("c", "configuration", true, "location of the config file");
				CommandLine line = argParser.parse( options, args );
				return line;
			}

}
