package org.kew.shs.dedupl.transformers.collations;

import org.kew.shs.dedupl.transformers.Transformer;
import org.kew.shs.dedupl.util.LibraryRegister;

@LibraryRegister(category="transformers")
public class YearExtractorTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		String[] collationElements = CollationUtils.parseCollation(s);
		StringBuffer sb = new StringBuffer();
		sb.append(collationElements[CollationUtils.YEAR1_INDEX]);
		if (!collationElements[CollationUtils.YEAR2_INDEX].isEmpty())
			sb.append(",").append(collationElements[CollationUtils.YEAR2_INDEX]);
		return sb.toString();
	}

}
