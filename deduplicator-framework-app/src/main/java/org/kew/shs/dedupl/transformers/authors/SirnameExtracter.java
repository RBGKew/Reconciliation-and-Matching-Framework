package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;

public class SirnameExtracter extends RegexDefCollection implements Transformer {

    public String transform(String s) {
        String chars = String.format("(?<!%s)(%s\\.\\s*)(?=[^$\\)])", ALPHANUMDIAC, ALPHANUMDIAC);
        return s.replaceAll(chars, "");
    }

}
