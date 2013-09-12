package org.kew.shs.dedupl.matchers;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.util.LibraryRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.ehcache.annotations.Cacheable;

import org.kew.shs.dedupl.util.Dictionary;

/**
 * This matcher uses the Levenshtein edit distance algorithm 
 * (provided by the Apache StringUtils class).  
 * It is computationally expensive, so has a cost of 10.
 * The maximum distance is configurable. 
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class LevenshteinMatcher implements Matcher {

	public static int COST = 10;
	private int maxDistance = 2;
	
	private int numCalls = 0;
	private int numExecutions = 0;

    private static Logger logger = LoggerFactory.getLogger(LevenshteinMatcher.class);
	
	private Dictionary dict;
	
	public Dictionary getDict() {
		return dict;
	}

	public void setDict(Dictionary dict) {
		this.dict = dict;
	}

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
				logger.debug("Testing ld(" + s1 + ", " + s2 + ")");
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
		if (matches){
			matches = doFalsePositiveCheck(s1,s2);
		}
		return matches;
	}
	
	private boolean doFalsePositiveCheck(String s1, String s2){
		boolean passed = true;
        Dictionary falsePositives = this.getDict();
		if (falsePositives != null){
			if (falsePositives.containsKey(s1) && falsePositives.get(s1).equals(s2))
				passed = false;
			else
				if (falsePositives.containsKey(s2) && falsePositives.get(s2).equals(s1))
					passed = false;
			if (passed == false){
				logger.info("Rejected match (" + s1 + ", " + s2 + ") as false positive");
			}
		}
		return passed;
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
