package org.kew.shs.dedupl.util;

/**
 * This is the main class that runs the deduplication.
 * It is configured using Spring, and all it does is get the 
 * Spring-defined deduplicator bean and run it.
 *
 */
import net.sf.ehcache.CacheManager;

import org.kew.shs.dedupl.DataMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MatchApp {

	public static void main(String[] args) {

		// This command picks up the Spring config file form the classpath
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"application-context-match.xml");

		/*
		 * Get the matcher bean from the Spring config - Spring has instantiated
		 * it with the values set in the application-context file.
		 * 
		 *  The string "matcher" corresponds to the id attribute of a bean element
		 *   defined in the Spring config file
		 */
		DataMatcher matcher = (DataMatcher) context.getBean("matcher");
		// Call the run method
		matcher.run();

		// Dump the cache statistics
		CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
	    String[] cacheNames = cacheManager.getCacheNames();
	    for (int i = 0; i < cacheNames.length; i++) {
	        String cacheName = cacheNames[i];
	        System.out.println(cacheName+" - "+ cacheManager.getCache(cacheName).getStatistics().toString());
	    }
	    
	}
	
}