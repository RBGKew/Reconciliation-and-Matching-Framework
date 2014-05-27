package org.kew.stringmod.lib.transformers.authors;

import org.kew.stringmod.lib.transformers.RegexDefCollection;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer tries to identify all surnames of plant name authors in a string
 * and deletes everything else.
 *
 * For examples see {@link SurnameExtractorTest}
 */
@LibraryRegister(category="transformers")
public class SurnameExtractor extends RegexDefCollection implements Transformer {

    @Override
    public String transform(String s) {
        // Linnaeus special: first we remove the Dot after <L> in order to make it appear as a surname,
        //  but only where it's very likely to be Linnaeus; afterwards we'll add it again.
        s = s.replaceAll(String.format("L\\.(?=\\s)", ALPHANUMDIAC), "L ");
        String chars = String.format("(?<!%s)(-*%s\\.\\s*)(?=[^$\\)])", ALPHANUMDIAC, ALPHANUMDIAC);
        s = s.replaceAll(chars, "");
        return s.replaceAll("L ", "L\\.");
    }
}
