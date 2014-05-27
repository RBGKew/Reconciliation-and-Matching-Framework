package org.kew.stringmod.lib.transformers.authors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.lib.transformers.authors.SurnameExtractor;

public class SurnameExtractorTest {

    @Test
    public void extractSimple() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "(R.W.Pohl) P.M.Peterson";
        assertEquals("(Pohl) Peterson", transformer.transform(author));
    }

    @Test
    public void extractNoBasionymNoSpaceBetweenFirstAbbrSurname() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "R.Goaverts";
        assertEquals("Goaverts", transformer.transform(author));
    }

    @Test
    public void extractNoBasionymSpaceBetweenFirstAbbrSurname() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "R. Goaverts";
        assertEquals("Goaverts", transformer.transform(author));
    }

    @Test
    public void extractNoBasionymTwoFirstAbbrvs() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "A.B.Cedrus";
        assertEquals("Cedrus", transformer.transform(author));
        author = "Hochst. ex A.Rich.";
        assertEquals("Hochst. ex Rich.", transformer.transform(author));
    }

    @Test
    public void extractShortSurname() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "(R.W.P.) L.";
        assertEquals("(P.) L.", transformer.transform(author));
    }

    @Test
    public void extractAbbrevSurname() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "(R.W.Po.) Ludov.";
        assertEquals("(Po.) Ludov.", transformer.transform(author));
    }

    @Test
    public void dealWithHyphenatedNames() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "J.-C.Belmondo & H.-J.Bladibla";
        assertEquals("Belmondo & Bladibla", transformer.transform(author));
        author = "E. Wainwright-Deri & F.-S.Schmidt-Soltau";
        assertEquals("Wainwright-Deri & Schmidt-Soltau", transformer.transform(author));
    }

    @Test
    public void additionalCases() throws Exception {
        Transformer transformer = new SurnameExtractor();
        String author = "L. ex Somebody";
        assertEquals("L. ex Somebody", transformer.transform(author));
        author = "L. in Somebody";
        assertEquals("L. in Somebody", transformer.transform(author));
        //author = "Kosnik, Diggs, Redshaw & L.L.Lipscomb";
        //assertEquals("Lipscomb", transformer.transform(author));
        author = "L.Diggs";
        assertEquals("Diggs", transformer.transform(author));
    }

}
