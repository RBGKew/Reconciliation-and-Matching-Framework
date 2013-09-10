package org.kew.shs.dedupl.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.kew.shs.dedupl.util.LibraryRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This matcher calculates how many tokens are shared between two strings, the tokens
 * If the calculated ratio is above the `minRatio` it returns true.
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class CommonTokensMatcher implements Matcher {

	public static int COST = 5;
	protected double minRatio = 0.5;
	private String delimiter = " ";
	
    private static Logger logger = LoggerFactory.getLogger(CommonTokensMatcher.class);
	
	public int getCost() {
		return COST;
	}
	
	/*@Cacheable(cacheName="ctMatchCache")*/
	public boolean matches(String s1, String s2) throws Exception {
		logger.debug("s1: " + s1);
		logger.debug("s2: " + s2);
		if (s1 == null && s2 == null) return true;
	    String[] a1 = convToArray(s1);
	    logger.debug(Arrays.toString(a1));
	    String[] a2 = convToArray(s2);
	    logger.debug(Arrays.toString(a2));
		return calculateTokensInCommon(a1,a2);
	}

	private String[] convToArray(String s){
		String[] a = s.split(this.delimiter);
		// if delimiter is blank we want every element to be represented as one item in the array;
		// however, the first element would be blank, which we correct here.
		if (this.getDelimiter() == "") a = Arrays.copyOfRange(a, 1, a.length);
		return a;
	}
	
	public Boolean calculateTokensInCommon(String[] s1, String[] s2){
		Collection<String> common = new ArrayList<String>(Arrays.asList(s1));
	    common.retainAll(Arrays.asList(s2));
	    int numCommon = common.size();
	    logger.debug("Number of tokens in common: " + numCommon);
	    
	    double ratio = (Double.valueOf(numCommon) / Double.valueOf((numCommon + (s1.length - numCommon) + (s2.length - numCommon))));
	    logger.debug("ratio = " + ratio);
	    
	    return ratio >= this.minRatio;
	}
	
	public double getMinRatio() {
		return this.minRatio;
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

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
}