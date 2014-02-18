package org.kew.stringmod.lib.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.lib.transformers.authors.DotFDotCleaner;

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
        author = "Baker f.";
        assertEquals("Baker", transformer.transform(author));
    }

}
