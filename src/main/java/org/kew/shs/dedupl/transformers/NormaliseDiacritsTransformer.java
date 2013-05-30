package org.kew.shs.dedupl.transformers;

import java.text.Normalizer;

public class NormaliseDiacritsTransformer implements Transformer {

	@Override
	public String transform(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		return s.replaceAll("[^\\p{ASCII}]", "");
	}

}
