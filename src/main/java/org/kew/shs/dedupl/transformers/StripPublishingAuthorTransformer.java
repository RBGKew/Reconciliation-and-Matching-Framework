package org.kew.shs.dedupl.transformers;

import org.apache.commons.lang.StringUtils;

/**
 * This transformer translates author strings in the form "(Author1) Author2" to "Author1"
 * @author nn00kg
 *
 */
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
