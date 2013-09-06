package org.kew.shs.dedupl.transformers;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class NormaliseDiacritsTransformer implements Transformer {

	@SuppressWarnings(value = { "serial" })
	static Map<String,String> ADDITIONAL_REPLACEMENTS = new HashMap<String,String>() {{
		put("Ø", "O");
		put("ø", "o");
		put("Ł", "L");
		put("ł", "l");
		put("—", "-"); // emdash to hyphen
		put("–", "-"); // ndash to hyphen
	}};

	@Override
	public String transform(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		for (String key:ADDITIONAL_REPLACEMENTS.keySet()) {
			s = s.replaceAll(key, ADDITIONAL_REPLACEMENTS.get(key));
		}
		return s.replaceAll("[^\\p{ASCII}]", "");
	}

}
