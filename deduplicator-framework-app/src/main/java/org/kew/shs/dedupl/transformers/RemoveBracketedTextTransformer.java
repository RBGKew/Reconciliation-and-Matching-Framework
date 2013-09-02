package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class RemoveBracketedTextTransformer extends RegexDefCollection implements Transformer {

    /*
     * Removes all text in brackets (round and square) incl. the brackets.
     *
     * TODO: not sure how that deals with nested structures, needs more testing
     */
    @Override
    public String transform(String s) {
        s = s.replaceAll(String.format("%s|%s", ROUND_BRACKETS_AND_CONTENT, SQUARE_BRACKETS_AND_CONTENT), "");
        return s.replaceAll("\\s+", " ").trim();
    }

}
