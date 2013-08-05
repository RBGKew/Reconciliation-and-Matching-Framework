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
