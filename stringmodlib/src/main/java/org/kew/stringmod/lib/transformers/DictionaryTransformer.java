package org.kew.stringmod.lib.transformers;

import java.io.IOException;

import org.kew.stringmod.utils.Dict;
import org.kew.stringmod.utils.LibraryRegister;


/**
 * Uses a {@link org.kew.stringmod.utils.Dict} object to lookup a string in its
 * keys and returns the value if the key is found. Otherwise it returns the
 * original string.
 */
@LibraryRegister(category="transformers")
public class DictionaryTransformer implements Transformer {

    Dict dict;
    private boolean fileLoaded = false;

    final public String transform (String s) throws IOException {
        if (!fileLoaded) {
            this.dict.readFile();
            this.fileLoaded = true;
        }
        return this.transformWithDict(s);
    }

    public String transformWithDict (String key) {
        String value = this.dict.get(key);
        return (value != null) ? value: key;
    }

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }

}
