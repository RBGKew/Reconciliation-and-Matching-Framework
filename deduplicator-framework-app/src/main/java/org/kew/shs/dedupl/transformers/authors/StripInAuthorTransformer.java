package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * This transformer translates author strings in the form "Author1 in Author2" to "Author1"
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class StripInAuthorTransformer extends RegexDefCollection implements Transformer{

    public String transform(String s) {
        String cleaned = s;
        if (s != null){
            if (s.toLowerCase().indexOf(IN_MARKER) != -1){
                cleaned = s.replaceAll(IN_MARKER_REGEX + ".*$", "");
            }
        }
        return cleaned;
    }

}
