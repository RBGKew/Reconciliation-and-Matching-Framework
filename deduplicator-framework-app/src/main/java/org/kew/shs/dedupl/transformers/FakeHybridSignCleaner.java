package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

/*
 * Replaces X and x that seem to be meant as hybrid signs
 *
 * X and x can be at the beginning of a string followed by a whitespace or
 * anywhere in the string if surrounded by white-spaces.
 */
@LibraryRegister(category="transformers")
public class FakeHybridSignCleaner implements Transformer {

    @Override
    public String transform(String s) {
        return s.replaceAll("^[Xx]\\s|\\s[xX]\\s", " ").replaceAll("\\s+",  " ").trim();
    }

}
