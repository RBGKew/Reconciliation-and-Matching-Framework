package org.kew.shs.dedupl.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * This matcher splits the input strings into tokens, and calculates 
 * how many tokens are shared between the two inputs.
 * The minimum ratio is configurable.
 * @author nn00kg
 *
 */
public class CommonTokensMatcher implements Matcher {

	public static int COST = 5;
	protected double minRatio=0.5;
	
	private static Logger log = Logger.getLogger(CommonTokensMatcher.class);
	
	public int getCost() {
		return COST;
	}
	
	/*@Cacheable(cacheName="ctMatchCache")*/
	public boolean matches(String s1, String s2) {
		log.debug("s1: " + s1);
		log.debug("s2: " + s2);
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			try{
			    String[] a1 = convToArray(s1);
			    log.debug(Arrays.toString(a1));
			    
			    String[] a2 = convToArray(s2);
			    log.debug(Arrays.toString(a2));
			    
			    matches = calculateTokensInCommon(a1,a2);
			}
			catch (Exception e) {
				;
			}
		}
		return matches;
	}

	private String[] convToArray(String s){
		return s.replaceAll("[^A-Za-z0-9]", " ").replaceAll("\\s+", " ").split(" ");
	}
	
	public Boolean calculateTokensInCommon(String[] s1, String[] s2){
		boolean matches = false;

		Collection<String> common = new ArrayList<String>(Arrays.asList(s1));
	    common.retainAll(Arrays.asList(s2));
	    int numCommon = common.size();
	    log.debug("Number of tokens in common: " + numCommon);
	    
	    double ratio = (Double.valueOf(numCommon) / Double.valueOf((numCommon + (s1.length - numCommon) + (s2.length - numCommon))));
	    log.debug("ratio = " + ratio);
	    //double ratio = (Double.valueOf(numCommon) / Double.valueOf(Math.max(s1.length, s2.length)));
	    
	    matches = ratio >= minRatio;
		
		return new Boolean(matches);
	}
	
	public double getMinRatio() {
		return minRatio;
	}

	public void setMinRatio(double minRatio) {
		this.minRatio = minRatio;
	}
	
	public boolean isExact() {
		return false;
	}
	
	public String getExecutionReport() {
		return null;
	}
	
}