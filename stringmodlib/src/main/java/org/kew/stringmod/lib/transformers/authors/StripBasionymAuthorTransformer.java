package org.kew.stringmod.lib.transformers.authors;

import org.apache.commons.lang.StringUtils;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer translates author strings in the form "(Author1) Author2" to "Author2"
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class StripBasionymAuthorTransformer implements Transformer{

	public String transform(String s) {
		if (StringUtils.isNotBlank(s))
			// replace ALL bits in brackets, then remove double whitespaces and surrounding whitespaces
			s = s.replaceAll("\\([^)]*\\)", "").replaceAll("\\s+", " ").trim();
		return s;
	}
	
	public static void main(String[] args) {
		StripBasionymAuthorTransformer t  = new StripBasionymAuthorTransformer(); 
		System.out.println(t.transform("(Author1) Author2"));
	}
}
