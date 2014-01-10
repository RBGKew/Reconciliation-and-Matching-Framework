package org.kew.shs.dedupl.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.kew.shs.dedupl.util.LibraryRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This matcher calculates how many tokens are shared between two strings, the tokens
 * If the calculated ratio is above the `minRatio` it returns true.
 * @author nn00kg
 *
 */
public abstract class TokeniserMatcher implements Matcher {

    private String delimiter = " ";

    private static Logger logger = LoggerFactory.getLogger(TokeniserMatcher.class);

    protected String[] convToArray(String s){
        String[] a = s.split(this.delimiter);
        // if delimiter is blank we want every element to be represented as one item in the array;
        // however, the first element would be blank, which we correct here.
        if (this.getDelimiter() == "") a = Arrays.copyOfRange(a, 1, a.length);
        return a;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}
