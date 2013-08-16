package org.kew.shs.dedupl.transformers.authors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.transformers.RegexDefCollection;
import org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer;
import org.kew.shs.dedupl.transformers.StringShrinker;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class ShrunkAuthors extends RegexDefCollection implements Transformer {

    private Integer shrinkTo = null;

    public String transform(String s) {
        s = new DotFDotCleaner().transform(s);
        s = new SirnameExtracter().transform(s);
        String pub = new StripBasionymAuthorTransformer().transform(s);
        String bas = new StripPublishingAuthorTransformer().transform(s);
        String exIn = String.format("%s|%s", EX_MARKER_REGEX, IN_MARKER_REGEX);
        List<String> sirNames = new ArrayList<>();
        for (String authors: new String[] {bas, pub}) {
            authors = authors.replaceAll(exIn, " ");
            authors = authors.replaceAll("\\s+", " ");
            for (String author:authors.split(" ")) {
                author = new SafeStripNonAlphaNumericsTransformer().transform(author);
                // shrink each identified author sirname to shrinkTo if set
                if (this.shrinkTo != null) author = new StringShrinker(this.shrinkTo).transform(author);
                if (!StringUtils.isBlank(author)) sirNames.add(author.trim());
            }
        }
        return StringUtils.join(sirNames, " ").toLowerCase();
    }

    public Integer getShrinkTo() {
        return shrinkTo;
    }

    public void setShrinkTo(Integer shrinkTo) {
        this.shrinkTo = shrinkTo;
    }

}
