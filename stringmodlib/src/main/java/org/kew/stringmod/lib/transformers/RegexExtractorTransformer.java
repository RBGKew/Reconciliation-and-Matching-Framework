package org.kew.stringmod.lib.transformers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * A generic transformer that extracts all occurrences of a pattern (regEx)
 * in a string
 *
 * It takes two optional parameters, `removeMultipleWhitespaces` (default true) and
 * `trimIt` (default true)
 */
@LibraryRegister(category="transformers")
public class RegexExtractorTransformer extends RegexDefCollection implements Transformer {

    protected String regex = "";
    private boolean removeMultipleWhitespaces = true;
    private boolean trimIt = true;

    @Override
    public String transform(String s) {
    	StringBuffer sb = new StringBuffer();
    	if (s != null){
            if (this.removeMultipleWhitespaces) s = s.replaceAll("\\s+", " ");
            if (this.trimIt) s = s.trim();
	    	Pattern patt = Pattern.compile("(" + regex + ")");
	    	Matcher m = patt.matcher(s);
	    	while (m.find()){
	    		if (sb.length() > 0) sb.append(" ");
	    		sb.append(m.group());
	    	}
    	}
        return sb.toString();
    }

    public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
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