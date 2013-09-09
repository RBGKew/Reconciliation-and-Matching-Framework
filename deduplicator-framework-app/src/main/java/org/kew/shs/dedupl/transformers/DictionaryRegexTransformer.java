package org.kew.shs.dedupl.transformers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kew.shs.dedupl.util.LibraryRegister;

/**
 * Uses a Dictionary object of which it iterate over the keys to use as regular
 * expression; if this pattern matches, it transforms the string accordingly.
 *
 * If multiTransform is set it goes on through the whole list of keys in the
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
