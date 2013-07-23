package org.kew.shs.dedupl.util;

/**
 * This is the main class that runs the matching.
 * It is configured using Spring, and all it does is get the
 * Spring-defined matcher bean and run it.
 *
 */
import org.springframework.context.ConfigurableApplicationContext;

public class MatchApp extends CoreApp {

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = getContext(args, "application-context-match.xml");

		// TODO: the whole differentiation into Dedup and Main could disappear and be decided upon the config-file
		//       contents
		runEngineAndCache(context);

	}

}
