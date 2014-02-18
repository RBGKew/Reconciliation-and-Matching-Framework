package org.kew.stringmod.lib.transformers;

import org.apache.commons.lang.StringUtils;
import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer translates zeros to blanks
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class ZeroToBlankTransformer implements Transformer{

	public String transform(String s) {
		String transformed = s;
		if (StringUtils.isNotBlank(s) & s.equals("0"))
			transformed = "";
		return transformed;
	}
	
}
