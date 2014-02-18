package org.kew.stringmod.lib.transformers.collations.wcs;

import org.kew.stringmod.lib.transformers.Transformer;
import org.kew.stringmod.utils.LibraryRegister;

@LibraryRegister(category="transformers")
public class VolExtractorTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		String[] collationElements = CollationUtils.parseCollation(s);
		return collationElements[CollationUtils.VOL_INDEX];
	}

}
