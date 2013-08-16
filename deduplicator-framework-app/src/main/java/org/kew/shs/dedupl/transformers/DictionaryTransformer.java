package org.kew.shs.dedupl.transformers;

import java.io.IOException;

import org.kew.shs.dedupl.util.Dictionary;
import org.kew.shs.dedupl.util.LibraryRegister;


@LibraryRegister(category="transformers")
public class DictionaryTransformer implements Transformer {

    Dictionary dict;
    private boolean fileLoaded = false;

    public String transform (String key) throws IOException {
        if (!fileLoaded) {
            this.dict.readFile();
            this.fileLoaded = true;
        }
        String value = this.dict.get(key);
        return (value != null) ? value: key;
    }

    public Dictionary getDict() {
        return dict;
    }

    public void setDict(Dictionary dict) {
        this.dict = dict;
    }

}
