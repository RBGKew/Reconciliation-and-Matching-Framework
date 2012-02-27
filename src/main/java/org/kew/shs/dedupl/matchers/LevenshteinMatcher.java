package org.kew.shs.dedupl.matchers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher uses the Levenshtein edit distance algorithm 
 * (provided by the Apache StringUtils class).  
 * It is computationally expensive, so has a cost of 10.
 * The maximum distance is configurable. 
 * @author nn00kg
 *
 */
public class LevenshteinMatcher implements Matcher {

	public static int COST = 10;
	private int maxDistance = 2;
	
	private int numCalls = 0;
	private int numExecutions = 0;

	private static Logger log = Logger.getLogger(LevenshteinMatcher.class);
	
	public int getCost() {
		return COST;
	}

	@Cacheable(cacheName="ldMatchCache")
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		numCalls++;
		if (StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2)){
			matches = s1.equals(s2); 
			if (!matches){
				log.debug("Testing ld(" + s1 + ", " + s2 + ")");
				int shorter = Math.min(s1.length(), s2.length());
				int longer = Math.max(s1.length(), s2.length());
				if ((longer - shorter) > maxDistance)
					matches = false;
				else{
					int distance = calculateLevenshtein(s1, s2).intValue();
					matches = (distance <= maxDistance);
				}
			}
		}
		return matches;
	}
	
	public Integer calculateLevenshtein(String s1, String s2){
		numExecutions++;
		return new Integer(StringUtils.getLevenshteinDistance(s1, s2));
	}
	
	public boolean isExact() {
		return false;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	public String getExecutionReport(){
		return "Number of calls: "+ numCalls + ", num executions: " + numExecutions;
	}
	
}