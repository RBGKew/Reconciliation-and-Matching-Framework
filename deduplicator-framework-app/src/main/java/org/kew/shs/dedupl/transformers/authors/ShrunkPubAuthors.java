package org.kew.shs.dedupl.transformers.authors;

import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.transformers.SafeStripNonAlphasTransformer;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;


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
                if (toShrink[i].length() > this.shrinkTo) toShrink[i] = toShrink[i].substring(0, this.shrinkTo);
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
