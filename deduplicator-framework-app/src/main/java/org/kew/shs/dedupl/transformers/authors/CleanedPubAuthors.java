package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * Cleans ex (StripExAuthor) after in (StripInAuthor) after removing the
 * basionym (StripBasionymAuthor).
 */
@LibraryRegister(category="transformers")
public class CleanedPubAuthors implements Transformer {

    public String transform (String s) {
        s = new StripBasionymAuthorTransformer().transform(s);
        s = new StripInAuthorTransformer().transform(s);
        return new StripExAuthorTransformer().transform(s);
    }

}
