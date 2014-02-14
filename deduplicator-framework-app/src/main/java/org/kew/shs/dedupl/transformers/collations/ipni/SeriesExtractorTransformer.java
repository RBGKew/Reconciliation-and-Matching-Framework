package org.kew.shs.dedupl.transformers.collations.ipni;

import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class SeriesExtractorTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		String[] collationElements = CollationUtils.parseCollation(s);
		return collationElements[CollationUtils.SERIES_INDEX];
	}

}
