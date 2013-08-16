package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.shs.dedupl.transformers.Transformer;

public class SirnameExtracterTest {

    @Test
    public void extractSimple() throws Exception {
        Transformer transformer = new SirnameExtracter();
        String author = "(R.W.Pohl) P.M.Peterson";
        assertEquals("(Pohl) Peterson", transformer.transform(author));
    }

    @Test
    public void extractNoBasionymNoSpaceBetweenFirstAbbrSirname() throws Exception {
        Transformer transformer = new SirnameExtracter();
        String author = "R.Goaverts";
        assertEquals("Goaverts", transformer.transform(author));
    }

    @Test
    public void extractNoBasionymSpaceBetweenFirstAbbrSirname() throws Exception {
        Transformer transformer = new SirnameExtracter();
        String author = "R. Goaverts";
        assertEquals("Goaverts", transformer.transform(author));
    }

    @Test
    public void extractNoBasionymTwoFirstAbbrvs() throws Exception {
        Transformer transformer = new SirnameExtracter();
        String author = "A.B.Cedrus";
        assertEquals("Cedrus", transformer.transform(author));
        author = "Hochst. ex A.Rich.";
        assertEquals("Hochst. ex Rich.", transformer.transform(author));
    }

    @Test
    public void extractShortSirname() throws Exception {
        Transformer transformer = new SirnameExtracter();
        String author = "(R.W.P.) L.";
        assertEquals("(P.) L.", transformer.transform(author));
    }

    @Test
    public void extractAbbrevSirname() throws Exception {
        Transformer transformer = new SirnameExtracter();
        String author = "(R.W.Po.) Ludov.";
        assertEquals("(Po.) Ludov.", transformer.transform(author));
    }


}
