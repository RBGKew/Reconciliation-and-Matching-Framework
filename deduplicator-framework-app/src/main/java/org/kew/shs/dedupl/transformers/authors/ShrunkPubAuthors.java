package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.NormaliseDiacritsTransformer;
import org.kew.shs.dedupl.transformers.Transformer;


public class ShrunkPubAuthors implements Transformer {

    private Integer shrinkTo = null;

    public String transform(String s) {
        s = new DotFDotCleaner().transform(s);
        s = new CleanedPubAuthors().transform(s);
        s = new SirnameExtracter().transform(s);
        s = new NormaliseDiacritsTransformer().transform(s);
        if (this.shrinkTo != null && s.length() > this.shrinkTo) {
            s = s.substring(0, this.shrinkTo);
        }
        return s;
    }

    public Integer getShrinkTo() {
        return shrinkTo;
    }

    public void setShrinkTo(Integer shrinkTo) {
        this.shrinkTo = shrinkTo;
    }

}
