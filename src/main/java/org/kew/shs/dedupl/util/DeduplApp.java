package org.kew.shs.dedupl.util;

/**
 * This is the main class that runs the deduplication.
 * It is configured using Spring, and all it does is get the
 * Spring-defined deduplicator bean and run it.
 *
 */
import net.sf.ehcache.CacheManager;

import org.apache.commons.cli.ParseException;
import org.kew.shs.dedupl.DataHandler;
import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;
import org.kew.shs.dedupl.lucene.LuceneDeduplicatorInvestigator;
import org.springframework.context.ConfigurableApplicationContext;

public class DeduplApp extends CoreApp {

	public static void main(String[] args) throws ParseException {

		ConfigurableApplicationContext context = getContext(args, "application-context-dedupl.xml");

		DataHandler engine = (DataHandler) context.getBean("engine");
		// Call the run method
		engine.run();

		DeduplicationConfiguration config = (DeduplicationConfiguration) context.getBean("config");

//		if (config.isWriteComparisonReport()){
//			LuceneDeduplicatorInvestigator i = (LuceneDeduplicatorInvestigator) context.getBean("investigator");
//			i.run();
//		}

		// Dump the cache statistics
		CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
	    String[] cacheNames = cacheManager.getCacheNames();
	    for (int i = 0; i < cacheNames.length; i++) {
	        String cacheName = cacheNames[i];
	        System.out.println(cacheName+" - "+ cacheManager.getCache(cacheName).getStatistics().toString());
	    }

	}

}
