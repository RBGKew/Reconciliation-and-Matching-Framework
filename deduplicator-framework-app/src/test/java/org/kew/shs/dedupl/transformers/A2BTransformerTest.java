package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.*;

import org.junit.Test;

public class A2BTransformerTest {

    @Test
    public void testSimple() {
        A2BTransformer transformer = new A2BTransformer();
        transformer.setA("Jelly");
        transformer.setB("Smelly");
        assertEquals("Smelly fish", transformer.transform("Jelly fish"));
    }

    @Test
    public void testRegEx() {
        A2BTransformer transformer = new A2BTransformer();
        transformer.setA("[^a-zA-Z\\s]");
        transformer.setB("");
        assertEquals("Tidy String", transformer.transform("Tidy 1251258215123412String..//**#"));
    }

}
