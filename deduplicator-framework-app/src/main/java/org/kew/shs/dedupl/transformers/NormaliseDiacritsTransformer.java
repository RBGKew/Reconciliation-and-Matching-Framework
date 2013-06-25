package org.kew.shs.dedupl.transformers;

import java.text.Normalizer;

public class NormaliseDiacritsTransformer implements Transformer {

	@Override
	public String transform(String s) {
		return Normalizer.normalize(s, Normalizer.Form.NFD);
	}

}
