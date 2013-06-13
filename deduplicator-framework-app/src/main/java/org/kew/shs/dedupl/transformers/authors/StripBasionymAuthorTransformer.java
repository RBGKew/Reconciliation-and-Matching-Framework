package org.kew.shs.dedupl.transformers.authors;

import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.transformers.Transformer;

/**
 * This transformer translates author strings in the form "(Author1) Author2" to "Author2"
 * @author nn00kg
 *
 */
public class StripBasionymAuthorTransformer implements Transformer{

	public String transform(String s) {
		String transformed = s;
		if (StringUtils.isNotBlank(s))
			transformed = s.substring(s.indexOf(")") + 1).trim();
		return transformed;
	}
	
	public static void main(String[] args) {
		StripBasionymAuthorTransformer t  = new StripBasionymAuthorTransformer(); 
		System.out.println(t.transform("(Author1) Author2"));
	}
}
