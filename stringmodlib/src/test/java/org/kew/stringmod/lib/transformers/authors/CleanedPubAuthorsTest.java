package org.kew.stringmod.lib.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.lib.transformers.authors.CleanedPubAuthors;

public class CleanedPubAuthorsTest {

    @Test
    public void cleanIt() throws Exception {
        Transformer transformer = new CleanedPubAuthors();
        String author = "(Bla Blub) NotSoImportant ex (Bli b. Bloe) MoreImportant in whatsoever";
        assertEquals("MoreImportant", transformer.transform(author));
    }

}
