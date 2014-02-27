package org.kew.stringmod.lib.transformers.authors;

import org.apache.commons.lang.StringUtils;
import org.kew.stringmod.lib.transformers.SafeStripNonAlphasTransformer;
import org.kew.stringmod.lib.transformers.StringShrinker;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;


/**
 * This transformer tries to identify all publication authors of plant names in
 * a string and returns a string where each of their sirnames are shrunk/cropped
 * to a length of `shrinkTo`.
 *
 * For examples of standard usage and corner cases see {@link
 * ShrunkPubAuthorsTest}
 */
@LibraryRegister(category="transformers")
public class ShrunkPubAuthors implements Transformer {

    private Integer shrinkTo = null;

    public String transform(String s) {
        s = new DotFDotCleaner().transform(s);
        s = new CleanedPubAuthors().transform(s);
        s = new SirnameExtracter().transform(s);
        s = new SafeStripNonAlphasTransformer().transform(s);
        s = s.replaceAll("\\s+", " ");
        // shrink each identified author sirname to shrinkTo if set
        if (this.shrinkTo != null) {
            String[] toShrink = s.split(" ");
            for (int i=0;i<toShrink.length;i++) {
                toShrink[i] = new StringShrinker(this.shrinkTo).transform(toShrink[i]);
            }
            s = StringUtils.join(toShrink, " ");
        }
        return s.toLowerCase();
    }

    public Integer getShrinkTo() {
        return shrinkTo;
    }

    public void setShrinkTo(Integer shrinkTo) {
        this.shrinkTo = shrinkTo;
    }

}
