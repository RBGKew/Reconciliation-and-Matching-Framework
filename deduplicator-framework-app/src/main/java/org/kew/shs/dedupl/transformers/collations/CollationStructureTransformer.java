package org.kew.shs.dedupl.transformers.collations;

import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class CollationStructureTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		return CollationUtils.assessCollationStructure(s);
	}

}
