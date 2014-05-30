package org.kew.stringmod.dedupl.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.kew.stringmod.utils.LibraryRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This matcher calculates how many tokens are shared between two strings, the tokens
 * If the calculated ratio is above the `minRatio` it returns true.
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class CommonTokensMatcher extends TokeniserMatcher {

    public static int COST = 5;
    protected double minRatio = 0.5;
    private boolean uniqueCommonTokens = false;

    private static Logger logger = LoggerFactory.getLogger(CommonTokensMatcher.class);

    @Override
    public int getCost() {
        return COST;
    }

    /*@Cacheable(cacheName="ctMatchCache")*/
    @Override
    public boolean matches(String s1, String s2) {
        logger.debug("s1: " + s1);
        logger.debug("s2: " + s2);
        if (s1 == null && s2 == null) return true;
        String[] a1 = convToArray(s1);
        logger.debug(Arrays.toString(a1));
        String[] a2 = convToArray(s2);
        logger.debug(Arrays.toString(a2));
        return calculateTokensInCommon(a1,a2);
    }

    public Boolean calculateTokensInCommon(String[] s1, String[] s2){
        Collection<String> common = new ArrayList<String>(Arrays.asList(s1));
        common.retainAll(Arrays.asList(s2));
        if (this.isUniqueCommonTokens()) common = new HashSet<String>(common);
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

    @Override
    public boolean isExact() {
        return false;
    }

    @Override
    public String getExecutionReport() {
        return null;
    }

    public boolean isUniqueCommonTokens() {
        return uniqueCommonTokens;
    }

    public void setUniqueCommonTokens(boolean uniqueCommonTokens) {
        this.uniqueCommonTokens = uniqueCommonTokens;
    }

}