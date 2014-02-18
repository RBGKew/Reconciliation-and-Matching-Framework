package org.kew.stringmod.lib.transformers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kew.stringmod.lib.transformers.EpithetTransformer;
import org.kew.stringmod.lib.transformers.Transformer;

public class EpithetTransformerTest {

    @Test
    public void test() throws Exception {
        Transformer transformer = new EpithetTransformer();
        assertEquals("Begonia", transformer.transform("Begonia2"));
        assertEquals("Begiana", transformer.transform("Begana"));
        assertEquals("Begiana", transformer.transform("Begiana"));
        assertEquals("Begocola", transformer.transform("Begocolus"));
        assertEquals("Begona", transformer.transform("Begonus"));
        assertEquals("Begona", transformer.transform("Begonum"));
        assertEquals("Bega", transformer.transform("Begon"));
        assertEquals("Begoni", transformer.transform("Begoniae"));
        assertEquals("Begoni", transformer.transform("Begonei"));
        assertEquals("Begoni", transformer.transform("Begonae"));
        assertEquals("Begoni", transformer.transform("Begonii"));
        assertEquals("Begonoi", transformer.transform("Begonioi"));
        assertEquals("Begoniia", transformer.transform("Begonija"));
        assertEquals("Begoniia", transformer.transform("Begoniya"));
        assertEquals("Begonia", transformer.transform("Begon-ia"));
        assertEquals("Begona", transformer.transform("Begon'a"));
        //assertEquals("Begianus", transformer.transform("Beganus"));
        //assertEquals("Begianum", transformer.transform("Beganum"));
        //assertEquals("Begiarum", transformer.transform("Begarum"));
        //assertEquals("Begoiorum", transformer.transform("Begorum"));
    }

}
