package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.Transformer;

/**
 * Cleans ex after in after removing the basionym.
 */
public class CleanedPubAuthor implements Transformer {

    public String transform (String s) {
        s = new StripBasionymAuthorTransformer().transform(s);
        s = new StripInAuthorTransformer().transform(s);
        return new StripExAuthorTransformer().transform(s);
    }

}
