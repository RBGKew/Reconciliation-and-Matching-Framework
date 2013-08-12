package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class ShrunkPubAuthorsTest {

    @Test
    public void shrinkEm() {
        Transformer transformer = new ShrunkPubAuthors();
        String author = "(A.B.Cyclus ex D.E. Fincus) G.H. Ictus.f.";
        assertEquals("ictus", transformer.transform(author));
        author = "T.Anderson ex Hook.f.";
        assertEquals("hook", transformer.transform(author));
    }

    @Test
    public void shrinkEmSmaller() {
        ShrunkPubAuthors transformer = new ShrunkPubAuthors();
        String author = "(A.B.Cyclus ex D.E. Fincus) G.H. Ictus.f.";
        assertEquals("ictus", transformer.transform(author));
        transformer.setShrinkTo(3);
        assertEquals("ict", transformer.transform(author));
    }

    @Test
    public void weirdCasesByRachel() {
        ShrunkPubAuthors transformer = new ShrunkPubAuthors();
        transformer.setShrinkTo(3);
        String author = "Bob,       Tony & John in Smith";
        assertEquals("bob ton joh", transformer.transform(author));
        author = "A.DC.";
        assertEquals("dc", transformer.transform(author));
        author = "Ă.Löve & D.Löve";
        assertEquals("lov lov", transformer.transform(author));
        author = "Benth.) Verdc.";
        assertEquals("ben ver", transformer.transform(author));
        author = "F.jr. ex Hook.f.";
        assertEquals("hoo", transformer.transform(author));
    }

    @Test
    public void hyphenProblems() {
        ShrunkPubAuthors transformer = new ShrunkPubAuthors();
        transformer.setShrinkTo(3);
        String author = "Hand.-Mazz.";
        assertEquals("han maz", transformer.transform(author));
        author = "Smith ex J.-F.Leroy";
        assertEquals("ler", transformer.transform(author));
        author = "A.St.-Hil.";
        assertEquals("st hil", transformer.transform(author));
        author = "B.-E.van Wyk in Smyth";
        assertEquals("van wyk", transformer.transform(author));
        author = "Grey-Wilson & Banks";
        assertEquals("gre wil ban", transformer.transform(author));
    }

}
