package org.kew.shs.dedupl.transformers.collations;

import org.kew.shs.dedupl.transformers.Transformer;

public class PageExtractorTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		String[] collationElements = CollationUtils.parseCollation(s);
		return collationElements[CollationUtils.PAGE_INDEX];
	}

}