package org.kew.shs.dedupl.transformers.authors;

import org.kew.shs.dedupl.transformers.Transformer;

/**
 * This transformer translates author strings in the form "Author1 ex Author2" to "Author2"
 * @author nn00kg
 *
 */
public class StripExAuthorTransformer implements Transformer{

	private static String EX_MARKER = " ex ";
	
	public String transform(String s) {
		String cleaned = s;
		if (s != null){
			if (s.indexOf(EX_MARKER) != -1){
				cleaned = s.replaceAll(".*" + EX_MARKER, "");
			}
		}
		return cleaned;	
	}

}