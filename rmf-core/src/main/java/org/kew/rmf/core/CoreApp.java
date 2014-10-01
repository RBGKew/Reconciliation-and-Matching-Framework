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
package org.kew.rmf.core;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Everything begins here.
 */
public class CoreApp {
	private static final Logger logger = LoggerFactory.getLogger(CoreApp.class);

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

	/**
	 * Get the "engine" bean from the Spring config - Spring has instantiated it
	 * with the values set in the application-context file, which defaults to
	 * deduplication mode. If the context provided is a matching one, the bean
	 * definition will be changed prior to its initialisation.
	 * @param context
	 * @throws Exception
	 */
	protected static void runEngineAndCache (ConfigurableApplicationContext context) throws Exception {
		DataHandler<?> engine = (DataHandler<?>) context.getBean("engine");
		// Call the run method
		engine.run();
	}
}
