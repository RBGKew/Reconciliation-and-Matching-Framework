package org.kew.stringmod.lib.transformers.authors;

import org.kew.stringmod.lib.transformers.RegexDefCollection;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;


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
