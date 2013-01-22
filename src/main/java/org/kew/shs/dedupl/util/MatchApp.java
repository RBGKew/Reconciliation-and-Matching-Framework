package org.kew.shs.dedupl.util;

/**
 * This is the main class that runs the matching.
 * It is configured using Spring, and all it does is get the
 * Spring-defined matcher bean and run it.
 *
 */

import net.sf.ehcache.CacheManager;

import org.apache.commons.cli.ParseException;
import org.kew.shs.dedupl.DataHandler;
import org.springframework.context.ConfigurableApplicationContext;

public class MatchApp extends CoreApp {

	public static void main(String[] args) throws ParseException {

		ConfigurableApplicationContext context = getContext(args, "application-context-match.xml");

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
