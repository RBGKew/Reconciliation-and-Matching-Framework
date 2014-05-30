package org.kew.stringmod.dedupl.matchers;

import org.kew.stringmod.lib.transformers.SafeStripNonAlphaNumericsTransformer;
import org.kew.stringmod.lib.transformers.authors.StripExAuthorTransformer;
import org.kew.stringmod.lib.transformers.authors.StripInAuthorTransformer;
import org.kew.stringmod.utils.LibraryRegister;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens in two authorship strings
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class AuthorCommonTokensMatcher extends CommonTokensMatcher{

    public static int COST = 1;

    final private StripInAuthorTransformer inCleaner = new StripInAuthorTransformer();
    final private StripExAuthorTransformer exCleaner = new StripExAuthorTransformer();
    final private SafeStripNonAlphaNumericsTransformer stripNonDs = new SafeStripNonAlphaNumericsTransformer();

    @Override
    public int getCost() {
        return COST;
    }

    @Override
    @Cacheable(cacheName="actMatchCache")
    public boolean matches(String s1, String s2) {
        boolean matches = false;
        if (s1 == null && s2 == null)
            matches = true;
        else{
            matches = super.matches(clean(s1),clean(s2));
        }
        return matches;
    }

    private String clean(String s) {
        return this.inCleaner.transform(this.exCleaner.transform(this.stripNonDs.transform(s)));
    }

    @Override
    public boolean isExact() {
        return false;
    }

    @Override
    public String getExecutionReport() {
        return null;
    }
}
