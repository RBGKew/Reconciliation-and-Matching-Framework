package org.kew.stringmod.lib.transformers;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * Converts a string to its normalised form using unicode's NFD form
 * (http://unicode.org/reports/tr15/) adding an ASCII equivalent after any
 * identified diacritical character) and then removes
 * all non-ASCII characters from the string
 *
 * In addition to the characters defined by the unicode consortium the following
 * characters are replaced: [[ADDITIONAL)_REPLACEMENTS]]
 */
@LibraryRegister(category="transformers")
public class NormaliseDiacritsTransformer implements Transformer {

    @SuppressWarnings(value = { "serial" })
    static Map<String,String> ADDITIONAL_REPLACEMENTS = new HashMap<String,String>() {{
        put("Ø", "O");
        put("ø", "o");
        put("Ł", "L");
        put("ł", "l");
        put("—", "-"); // emdash to hyphen
        put("–", "-"); // ndash to hyphen
    }};

    @Override
    public String transform(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        for (String key:ADDITIONAL_REPLACEMENTS.keySet()) {
            s = s.replaceAll(key, ADDITIONAL_REPLACEMENTS.get(key));
        }
        return s.replaceAll("[^\\p{ASCII}]", "");
    }

}
