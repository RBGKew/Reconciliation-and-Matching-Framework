package org.kew.stringmod.lib.transformers.authors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This transformer tries to identify all Sirnames of plant name authors in a string
 * and deletes everything else.
 *
 * For examples see {@link SurnameExtractorTest}
 * @deprecated use {@link SurnameExtractor} instead.
 */
public class SirnameExtracter extends SurnameExtractor {
	private static Logger logger = LoggerFactory.getLogger(SirnameExtracter.class);

	public SirnameExtracter() {
		logger.warn("\"SirnameExtracter\" should be changed to \"SurnameExtractor\"");
	}
}
