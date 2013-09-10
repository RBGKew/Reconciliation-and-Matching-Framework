package org.kew.shs.dedupl.transformers;

public class CapitalLettersTransformer extends A2BTransformer {

    final private String a = "[^A-Z]";
    private String b = " ";

    // Without specifying the getters here Java inheritance would return `a` and
    // `b` of the superclass as the getters are defined there, how strange is
    // that..
    public String getA() {
        return this.a;
    }
    public String getB() {
        return this.b;
    }

}
