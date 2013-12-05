package org.kew.shs.dedupl.transformers.collations;

import org.kew.shs.dedupl.transformers.Transformer;

public class IssueExtractorTransformer implements Transformer{

	@Override
	public String transform(String s) throws Exception {
		String[] collationElements = CollationUtils.parseCollation(s);
		return collationElements[CollationUtils.ISSUE_INDEX];
	}

}
