package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class CleanedPubAuthorTest {

    @Test
    public void cleanIt() {
        Transformer transformer = new CleanedPubAuthor();
        String author = "(Bla Blub) NotSoImportant ex (Bli b. Bloe) MoreImportant in whatsoever";
        assertEquals("MoreImportant", transformer.transform(author));
    }

}
