package org.kew.shs.dedupl.transformers;

import static org.junit.Assert.*;

import org.junit.Test;

public class FakeHybridSignCleanerTest {

    @Test
    public void test() throws Exception {
        Transformer transformer = new FakeHybridSignCleaner();
        assertEquals("bladibla", transformer.transform("X bladibla"));
        assertEquals("bladibla", transformer.transform("x bladibla"));
        assertEquals("bladi bla", transformer.transform("bladi x bla"));
        assertEquals("bladi bla", transformer.transform("bladi X bla"));

        assertEquals("bladix bla", transformer.transform("bladix bla"));
        assertEquals("bladiX bla", transformer.transform("bladiX bla"));
    }

}
