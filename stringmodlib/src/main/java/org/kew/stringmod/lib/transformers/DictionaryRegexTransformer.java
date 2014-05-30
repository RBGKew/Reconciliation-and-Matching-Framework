package org.kew.stringmod.lib.transformers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * Uses a {@link org.kew.stringmod.utils.Dict} object of which it iterates over
 * the keys to use each as a regular expression; if the pattern matches, it
 * transforms the string accordingly returning the corresponding value of the
 * Dictionary.
 *
 * If multiTransform is set it goes through the whole list of keys in the
 * same way, otherwise it returns after the first match.
 */
@LibraryRegister(category="transformers")
public class DictionaryRegexTransformer extends DictionaryTransformer {

    private boolean multiTransform = false;

    @Override
    public String transformWithDict(String s) {
        for (Map.Entry<String, String> entry:this.dict.entrySet()) {
            Pattern p = Pattern.compile(entry.getKey());
            Matcher m = p.matcher(s);
            if (m.find()) {
                s = m.replaceAll(entry.getValue());
                if (this.multiTransform == false) return s;
            }
        }
        return s;
    }

    public boolean isMultiTransform() {
        return multiTransform;
    }

    public void setMultiTransform(boolean multiTransform) {
        this.multiTransform = multiTransform;
    }
}
