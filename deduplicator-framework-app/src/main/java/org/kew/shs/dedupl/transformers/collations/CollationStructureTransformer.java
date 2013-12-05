package org.kew.shs.dedupl.transformers.collations;

import org.kew.shs.dedupl.transformers.Transformer;

public class CollationStructureTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		return CollationUtils.assessCollationStructure(s);
	}

}
