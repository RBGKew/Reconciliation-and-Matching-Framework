package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.Transformer;


public class DotFDotCleaner implements Transformer {

    public String transform(String s) {
        String uppercase_onelettersirname = "(?<=[A-ZÁÉÍÓÚÂÊÎÔÃÕÇ])\\.f\\.";
        String lowercase_moreletters = "(?<=[a-zwáéíóúâêîôãõç][a-zwáéíóúâêîôãõç])\\.f\\.";

        String c = String.format("%s|%s", uppercase_onelettersirname, lowercase_moreletters);
        return s.replaceAll(c, "");
    }

}
