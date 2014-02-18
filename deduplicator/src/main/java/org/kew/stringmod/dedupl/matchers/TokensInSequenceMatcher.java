package org.kew.stringmod.dedupl.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kew.stringmod.lib.transformers.StripNonNumericCharactersTransformer;
import org.kew.stringmod.utils.LibraryRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This matcher returns a match if the shorter list of tokens occurs in order 
 * in the longer list of tokens - e.g. "10 2 140" and "10 140" will match, but 
 * "10 2 140" and "10 141" will not. 
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class TokensInSequenceMatcher extends TokeniserMatcher {

    public static int COST = 5;

    private static Logger logger = LoggerFactory.getLogger(TokensInSequenceMatcher.class);

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
        return calculateTokensInSequence(a1,a2);
    }

    public Boolean calculateTokensInSequence(String[] s1, String[] s2){
    	List<String> superset = null;
    	List<String> subset = null;
    	
    	if (s1.length > s2.length){
    		superset = new ArrayList<String>(Arrays.asList(s1));
    		subset = new ArrayList<String>(Arrays.asList(s2));;
    	}
    	else{
    		superset = new ArrayList<String>(Arrays.asList(s2));;
    		subset = new ArrayList<String>(Arrays.asList(s1));    		
    	}
    	boolean tokensInSeq = false;
    	for (String s : superset){
    		if (subset.size() > 0){
	    		if (s.equals(subset.get(0)))
	    			subset.remove(0);
    		}
    	}
    	tokensInSeq = (subset.size() == 0);
        return tokensInSeq;
    }

    public boolean isExact() {
        return false;
    }

    public String getExecutionReport() {
        return null;
    }

    public static void main(String[] args) {
		TokensInSequenceMatcher tsm = new TokensInSequenceMatcher();
		StripNonNumericCharactersTransformer stripper = new StripNonNumericCharactersTransformer();
		tsm.setDelimiter(" ");
		try{
			System.out.println(tsm.matches(stripper.transform("10(1): 121"), stripper.transform("10: 121")));
			System.out.println(tsm.matches(stripper.transform("10(1): 122"), stripper.transform("10: 122")));
			System.out.println(tsm.matches(stripper.transform("10(2): 122"), stripper.transform("10: 122")));
			System.out.println(tsm.matches(stripper.transform("10(2): 122"), stripper.transform("10: 124")));
			System.out.println(tsm.matches("1 3", "1 2 3"));
			System.out.println(tsm.matches("1", "1 2 3"));
			System.out.println(tsm.matches("", "1 2 3"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}