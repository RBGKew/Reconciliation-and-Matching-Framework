package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class SirnameExtracter extends RegexDefCollection implements Transformer {

    public String transform(String s) {
        //TODO: doesn't work!!// whitespace between potential sirnames without ampersand or comma shall be overwritten by a hyphen; these are one sirname!
        //s = s.replaceAll(String.format("(?<=\\w|[^\\%s\\%s,&])\\s(?=\\w[^\\%s\\%s,&])", IN_MARKER, EX_MARKER, IN_MARKER, EX_MARKER), "-");
        // Linnaeus special: first we remove the Dot after <L> in order to make it appear as a sirname
        s = s.replaceAll("L\\.", "L ");
        String chars = String.format("(?<!%s)(%s\\.\\s*)(?=[^$\\)])", ALPHANUMDIAC, ALPHANUMDIAC);
        s = s.replaceAll(chars, "");
        return s.replaceAll("L ", "L\\.");
    }

}
