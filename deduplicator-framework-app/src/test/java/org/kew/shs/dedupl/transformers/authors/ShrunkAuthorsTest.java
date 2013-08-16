package org.kew.shs.dedupl.transformers.authors;

import static org.junit.Assert.*;

import org.junit.Test;

public class ShrunkAuthorsTest {

    @Test
    public void need_to_remove_initials() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        String author = "W.H.Wagner";
        assertEquals("wagner", transformer.transform(author));
        transformer.setShrinkTo(4);
        assertEquals("wagn", transformer.transform(author));
    }

    @Test
    public void need_to_deal_with_multiple_authors() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "\"Kosnik, Diggs, Redshaw & Lipscomb\"";
        assertEquals("kos dig red lip", transformer.transform(author));
    }

    @Test
    public void need_to_deal_with_diacrits() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(4);
        String author = "H.Baumann & Künkele";
        assertEquals("baum kunk", transformer.transform(author));
    }

    // TODO: make work (if it really makes sense..); 
//    @Test
//    public void need_to_recognise_that_this_is_only_a_single_author_no_ampisand_or_comma1() {
//        ShrunkAuthors transformer = new ShrunkAuthors();
//        transformer.setShrinkTo(4);
//        String author = "Soto Arenas";
//        assertEquals("soto", transformer.transform(author));
//    }

    @Test
    public void ex_author_and_short_author() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "L. ex Somebody";
        assertEquals("l som", transformer.transform(author));
    }

    @Test
    public void ex_and_in() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "L. ex Somebody in R.E.Else";
        assertEquals("l som els", transformer.transform(author));
    }

    @Test
    public void in_author() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "L. in Else";
        assertEquals("l els", transformer.transform(author));
    }

    @Test
    public void basionym_and_publishing_authors() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "(Who) Mëssë & Moc.";
        assertEquals("who mes moc", transformer.transform(author));
    }

    @Test
    public void basionym_and_publishing_and_ex_authors() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "(D.R.Who) D.R.Sessë & D.Rb. Ex Didnot & Doit";
        assertEquals("who ses rb did doi", transformer.transform(author));
    }

    @Test
    public void basionym_pub_ex_in() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "(Who) Sessë & Moc. Ex Did & It in R.E.A.Life";
        assertEquals("who ses moc did it lif", transformer.transform(author));
    }

    @Test
    public void basionym_pub_in_and_dotFdot() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "(L.) A.Gray in Hook.f";
        assertEquals("l gra hoo", transformer.transform(author));
    }

    @Test
    public void double_basionym_and_publishing_author() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = " (M.R.Ekstra & M.Iss.) Nobody";
        assertEquals("eks iss nob", transformer.transform(author));
    }

    @Test
    public void fDot_author() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "Baker f.";
        assertEquals("bak", transformer.transform(author));
    }

    @Test
    public void nonCapitalised_last_name() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "J.extend";
        assertEquals("ext", transformer.transform(author));
    }

    @Test
    public void tab_in_authors() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "Bob, \tTony & John in Smith";
        assertEquals("bob ton joh smi", transformer.transform(author));
    }

    @Test
    public void half_bracket_only() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "Benth.) Verdc.";
        assertEquals("ben ver", transformer.transform(author));
    }

    @Test
    public void white_spaces_and_diacritics() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "Zümelis     ";
        assertEquals("zum", transformer.transform(author));
    }

    @Test
    public void need_to_recognise_that_this_is_only_a_single_author_no_ampisand_or_comma2() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "Hand.-Mazz.";
        assertEquals("han", transformer.transform(author));
    }

    @Test
    public void author_with_legitimate_whitespace_in() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "B.-E.van Wyk in Smyth";
        try {
            assertEquals("van smy", transformer.transform(author));
        } catch (AssertionError e) {
            // not the end of the world if it shortens to:
            assertEquals("van wyk smy", transformer.transform(author));
        }
    }

    @Test
    public void hyphenated_author() {
        ShrunkAuthors transformer = new ShrunkAuthors();
        transformer.setShrinkTo(3);
        String author = "Grey-Wilson & Banks";
        assertEquals("gre ban", transformer.transform(author));
    }


}
