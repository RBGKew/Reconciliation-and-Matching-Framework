package org.kew.stringmod.lib.transformers.authors;

import org.apache.commons.lang.StringUtils;
import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer translates author strings in the form "(Author1) Author2" to "Author1"
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class StripPublishingAuthorTransformer implements Transformer{

	public String transform(String s) {
		String transformed = s;
		if (StringUtils.isNotBlank(s) && ((s.indexOf("(") != -1) && s.indexOf(")") != -1))
			transformed = s.substring(s.indexOf("(")+1,s.indexOf(")"));
		else
			transformed = "";
		return transformed;
	}
	
	public static void main(String[] args) {
		StripPublishingAuthorTransformer t  = new StripPublishingAuthorTransformer(); 
		System.out.println(t.transform("(Author1) Author 2"));
		System.out.println(t.transform("Author3"));
		System.out.println(t.transform("(Author4)"));
	}
	
}
