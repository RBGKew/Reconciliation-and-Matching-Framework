package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;


/**
 * Removes " f.", ".f." after any alphanumeric Characters (incl. diacritics).
 */
@LibraryRegister(category="transformers")
public class DotFDotCleaner extends RegexDefCollection implements Transformer {

    public String transform(String s) {
        String c = String.format("(?<=%s)[\\.\\s]f\\.", ALPHANUMDIAC);
        return s.replaceAll(c, "");
    }

}
