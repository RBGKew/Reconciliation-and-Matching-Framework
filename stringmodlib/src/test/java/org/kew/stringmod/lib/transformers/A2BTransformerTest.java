package org.kew.stringmod.lib.transformers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kew.stringmod.lib.transformers.A2BTransformer;

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
