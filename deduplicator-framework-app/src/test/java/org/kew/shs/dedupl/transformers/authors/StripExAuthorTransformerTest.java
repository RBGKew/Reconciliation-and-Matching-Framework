package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class StripExAuthorTransformerTest {

    @Test
    public void stripItSimple() throws Exception {
        Transformer transformer = new StripExAuthorTransformer();
        String author = "Someone ex Else";
        assertEquals("Else", transformer.transform(author));
    }

    @Test
    public void capitalEx() throws Exception {
        Transformer transformer = new StripExAuthorTransformer();
        String author = "Someone ex Else";
        assertEquals("Else", transformer.transform(author));
    }

}
