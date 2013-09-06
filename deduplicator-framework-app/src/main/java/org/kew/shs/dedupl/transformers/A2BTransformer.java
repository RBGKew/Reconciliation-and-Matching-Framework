package org.kew.shs.dedupl.transformers;

public class A2BTransformer implements Transformer {

    private String a = "";
    private String b = "";

    public String transform(String s) {
        return s.replaceAll(a, b);
    }

    public String getA() {
        return a;
    }
    public void setA(String a) {
        this.a = a;
    }
    public String getB() {
        return b;
    }
    public void setB(String b) {
        this.b = b;
    }

}
