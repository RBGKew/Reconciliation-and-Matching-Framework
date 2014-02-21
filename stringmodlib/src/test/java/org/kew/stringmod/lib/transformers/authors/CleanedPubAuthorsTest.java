package org.kew.stringmod.lib.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.stringmod.lib.transformers.Transformer;

public class CleanedPubAuthorsTest {

    @Test
    public void cleanIt() throws Exception {
        Transformer transformer = new CleanedPubAuthors();
        String author = "(Bla Blub) NotSoImportant ex (Bli b. Bloe) MoreImportant in whatsoever";
        assertEquals("MoreImportant", transformer.transform(author));
    }

    @Test
    public void cleanFirstExThenIn() throws Exception {
        Transformer transformer = new CleanedPubAuthors();
        // standard case:
        String author = "XY ex Z in B";
        assertEquals("Z", transformer.transform(author));
        // sometimes the wrong way round:
        author = " XY in Z ex B";
        assertEquals("B", transformer.transform(author));
    }

}
