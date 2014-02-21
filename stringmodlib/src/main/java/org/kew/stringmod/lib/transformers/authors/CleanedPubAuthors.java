package org.kew.stringmod.lib.transformers.authors;

import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

/**
 * Cleans ex (StripExAuthor) after in (StripInAuthor) after removing the
 * basionym (StripBasionymAuthor).
 */
@LibraryRegister(category="transformers")
public class CleanedPubAuthors implements Transformer {

    public String transform (String s) {
        s = new StripBasionymAuthorTransformer().transform(s);
        s = new StripExAuthorTransformer().transform(s);
        return new StripInAuthorTransformer().transform(s);
    }

}
