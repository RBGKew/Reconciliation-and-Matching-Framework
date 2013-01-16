package org.kew.shs.dedupl.util;

/**
 * This is the main class that runs the deduplication.
 * It is configured using Spring, and all it does is get the 
 * Spring-defined deduplicator bean and run it.
 *
 */
import net.sf.ehcache.CacheManager;

import org.kew.shs.dedupl.Deduplicator;
import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;
import org.kew.shs.dedupl.lucene.LuceneDeduplicatorInvestigator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeduplApp {

	public static void main(String[] args) {

		// This command picks up the Spring config file form the classpath
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"application-context-dedupl.xml");

		/*
		 * Get the deduplicator bean from the Spring config - Spring has instantiated
		 * it with the values set in the application-context file.
		 * 
		 *  The string "deduplicator" corresponds to the id attribute of a bean element
		 *   defined in the Spring config file
		 */
		Deduplicator deduplicator = (Deduplicator ) context.getBean("deduplicator");
		// Call the run method
		deduplicator.run();

		DeduplicationConfiguration config = (DeduplicationConfiguration) context.getBean("config");
		
		if (config.isWriteComparisonReport()){
			LuceneDeduplicatorInvestigator i = (LuceneDeduplicatorInvestigator) context.getBean("investigator");
			i.run();
		}
		
		// Dump the cache statistics
		CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
	    String[] cacheNames = cacheManager.getCacheNames();
	    for (int i = 0; i < cacheNames.length; i++) {
	        String cacheName = cacheNames[i];
	        System.out.println(cacheName+" - "+ cacheManager.getCache(cacheName).getStatistics().toString());
	    }
	    
	}

}