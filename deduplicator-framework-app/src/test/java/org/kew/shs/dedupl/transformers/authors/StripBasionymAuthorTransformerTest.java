package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class StripBasionymAuthorTransformerTest {

    @Test
    public void stripItSimple() throws Exception {
        Transformer transformer = new StripBasionymAuthorTransformer();
        String author = "(WeirdBasionymGuy In. Brackets) TheOneWeWant";
        assertEquals("TheOneWeWant", transformer.transform(author));
    }

    @Test
    public void stripItMultipleAuthors () throws Exception {
        Transformer transformer = new StripBasionymAuthorTransformer();
        String author = "(WeirdBasionymGuy In. Brackets) TheOneWeWant in (AnotherBasionymGuy I. Brackets) ThisOneWeWantAsWell";
        assertEquals("TheOneWeWant in ThisOneWeWantAsWell", transformer.transform(author));
    }

}
