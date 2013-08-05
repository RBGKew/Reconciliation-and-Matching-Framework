package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;


public class DotFDotCleaner extends RegexDefCollection implements Transformer {

    public String transform(String s) {
        String c = String.format("(?<=%s)\\.f\\.", ALPHANUMDIAC);
        return s.replaceAll(c, "");
    }

}
