package org.kew.stringmod.lib.transformers.collations.ipni;

import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

@LibraryRegister(category="transformers")
public class SeriesExtractorTransformer implements Transformer{

	@Override
	public String transform(String s) {
		String[] collationElements = CollationUtils.parseCollation(s);
		return collationElements[CollationUtils.SERIES_INDEX];
	}
}
