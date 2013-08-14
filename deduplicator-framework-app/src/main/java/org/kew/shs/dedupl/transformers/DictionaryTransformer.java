package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.Dictionary;


public class DictionaryTransformer implements Transformer {

    Dictionary dict;

    public String transform (String key) {
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
