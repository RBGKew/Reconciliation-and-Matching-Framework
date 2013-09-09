package org.kew.shs.dedupl.matchers;

import org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer;
import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.transformers.authors.StripExAuthorTransformer;
import org.kew.shs.dedupl.transformers.authors.StripInAuthorTransformer;
import org.kew.shs.dedupl.util.LibraryRegister;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens in two authorship strings
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class AuthorCommonTokensMatcher extends CommonTokensMatcher{

    public static int COST = 1;

    final private Transformer inCleaner = new StripInAuthorTransformer();
    final private Transformer exCleaner = new StripExAuthorTransformer();
    final private Transformer stripNonDs = new SafeStripNonAlphaNumericsTransformer();

    public int getCost() {
        return COST;
    }

    @Cacheable(cacheName="actMatchCache")
    public boolean matches(String s1, String s2) throws Exception {
        boolean matches = false;
        if (s1 == null && s2 == null)
            matches = true;
        else{
            matches = super.matches(clean(s1),clean(s2));
        }
        return matches;
    }

    private String clean(String s) throws Exception{
        return this.inCleaner.transform(this.exCleaner.transform(this.stripNonDs.transform(s)));
    }

    public boolean isExact() {
        return false;
    }

    public String getExecutionReport() {
        return null;
    }

}
