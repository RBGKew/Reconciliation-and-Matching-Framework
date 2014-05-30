package org.kew.stringmod.lib.transformers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * A generic transformer that searches for all occurrences of a pattern (regEx)
 * `a` in a string and replaces each with a string `b`;
 *
 * It takes two optional parameters, `removeMultipleWhitespaces` (default true) and
 * `trimIt` (default true)
 */
@LibraryRegister(category="transformers")
public class A2BTransformer extends RegexDefCollection implements Transformer {

    protected String a = "";
    protected String b = "";
    private boolean removeMultipleWhitespaces = true;
    private boolean trimIt = true;

    @Override
    public String transform(String s) {
        s = s.replaceAll(this.getA(), this.getB());
        if (this.removeMultipleWhitespaces) s = s.replaceAll("\\s+", " ");
        if (this.trimIt) s = s.trim();
        return s;
    }

    public String getA() {
        return a;
    }
    public void setA(String a) {
        this.a = a;
    }
    public String getB() {
        return b;
    }
    public void setB(String b) {
        this.b = b;
    }

    public boolean isRemoveMultipleWhitespaces() {
        return removeMultipleWhitespaces;
    }

    public void setRemoveMultipleWhitespaces(boolean removeMultipleWhitespaces) {
        this.removeMultipleWhitespaces = removeMultipleWhitespaces;
    }

    public boolean isTrimIt() {
        return trimIt;
    }

    public void setTrimIt(boolean trimIt) {
        this.trimIt = trimIt;
    }
}
