package org.kew.rmf.matchers;

import java.util.Arrays;


/**
 * This matcher calculates how many tokens are shared between two strings, the tokens
 * If the calculated ratio is above the `minRatio` it returns true.
 * @author nn00kg
 *
 */
public abstract class TokeniserMatcher implements Matcher {

    private String delimiter = " ";

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
