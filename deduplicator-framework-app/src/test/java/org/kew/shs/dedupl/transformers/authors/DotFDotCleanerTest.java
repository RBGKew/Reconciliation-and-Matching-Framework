package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class DotFDotCleanerTest {

    @Test
    public void cleanIt() throws Exception {
        Transformer transformer = new DotFDotCleaner();
        String author = "Hans.f. bla";
        assertEquals("Hans bla", transformer.transform(author));
        author = "T.Anderson ex Hook.f.";
        assertEquals("T.Anderson ex Hook", transformer.transform(author));
        author = "Kauffman";
        assertEquals("Kauffman", transformer.transform(author));
    }

    @Test
    public void alsoCleanSpaceFDot() throws Exception {
        Transformer transformer = new DotFDotCleaner();
        String author = "Hans f. bla";
        assertEquals("Hans bla", transformer.transform(author));
    }

}
