package org.kew.stringmod.lib.transformers;

/**
 * CapitalLettersExtractor returns only the capital letters in a string.
 *
 * This is achieved by extending {@link A2BTransformer}, where the pattern to
 * search for (`a`) is fixed as "[^A-Z]" (everything but capital letters), and
 * the replacement (`b`) defaults to an empty string but can be overwritten.
 */
public class CapitalLettersExtractor extends A2BTransformer {

    final private String a = "[^A-Z]";
    private String b = " ";

    // Without specifying the getters here Java inheritance would return `a` and
    // `b` of the superclass as the getters are defined there, how strange is
    // that..
    @Override
    public String getA() {
        return this.a;
    }

    @Override
    public String getB() {
        return this.b;
    }

    @Override
    public void setB(String b) {
        this.b = b;
    }
}
