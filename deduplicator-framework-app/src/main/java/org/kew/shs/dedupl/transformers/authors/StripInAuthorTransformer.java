package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.Transformer;

/**
 * This transformer translates author strings in the form "Author1 in Author2" to "Author1"
 * @author nn00kg
 *
 */
public class StripInAuthorTransformer implements Transformer{

	private static String IN_MARKER = " in ";
	
	public String transform(String s) {
		String cleaned = s;
		if (s != null){
			if (s.indexOf(IN_MARKER) != -1){
				cleaned = s.replaceAll(IN_MARKER+ ".*$", "");
			}
		}
		return cleaned;		
	}

}
