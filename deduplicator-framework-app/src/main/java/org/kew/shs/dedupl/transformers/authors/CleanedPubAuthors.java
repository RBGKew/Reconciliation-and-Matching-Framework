package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * Cleans ex after in after removing the basionym.
 */
@LibraryRegister(category="transformers")
public class CleanedPubAuthors implements Transformer {

    public String transform (String s) {
        s = new StripBasionymAuthorTransformer().transform(s);
        s = new StripInAuthorTransformer().transform(s);
        return new StripExAuthorTransformer().transform(s);
    }

}
