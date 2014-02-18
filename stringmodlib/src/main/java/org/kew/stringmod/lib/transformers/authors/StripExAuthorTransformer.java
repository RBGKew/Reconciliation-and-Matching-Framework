package org.kew.stringmod.lib.transformers.authors;

import org.kew.stringmod.lib.transformers.RegexDefCollection;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

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
