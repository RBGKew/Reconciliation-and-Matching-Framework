package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class SirnameExtracter extends RegexDefCollection implements Transformer {

    public String transform(String s) {
        String chars = String.format("(?<!%s)(%s\\.\\s*)(?=[^$\\)])", ALPHANUMDIAC, ALPHANUMDIAC);
        return s.replaceAll(chars, "");
    }

}
