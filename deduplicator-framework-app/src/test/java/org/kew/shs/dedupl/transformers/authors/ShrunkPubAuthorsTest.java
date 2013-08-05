package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class ShrunkPubAuthorsTest {

    @Test
    public void shrinkEm() {
        Transformer transformer = new ShrunkPubAuthors();
        String author = "(A.B.Cyclus ex D.E. Fincus) G.H. Ictus.f.";
        assertEquals("Ictus", transformer.transform(author));
        author = "T.Anderson ex Hook.f.";
        assertEquals("Hook", transformer.transform(author));
    }

    @Test
    public void shrinkEmSmaller() {
        ShrunkPubAuthors transformer = new ShrunkPubAuthors();
        String author = "(A.B.Cyclus ex D.E. Fincus) G.H. Ictus.f.";
        assertEquals("Ictus", transformer.transform(author));
        transformer.setShrinkTo(3);
        assertEquals("Ict", transformer.transform(author));
    }

}
