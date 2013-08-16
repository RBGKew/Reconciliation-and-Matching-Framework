package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * This transformer translates author strings in the form "Author1 ex Author2" to "Author2"
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class StripExAuthorTransformer extends RegexDefCollection implements Transformer{

    public String transform(String s) {
        String cleaned = s;
        if (s != null){
            if (s.toLowerCase().indexOf(EX_MARKER) != -1){
                cleaned = s.replaceAll(".*" + EX_MARKER_REGEX, "");
            }
        }
        return cleaned;
    }

}
