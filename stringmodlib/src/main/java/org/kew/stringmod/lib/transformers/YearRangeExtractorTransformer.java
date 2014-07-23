package org.kew.stringmod.lib.transformers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * Extracts year ranges from arbitrary strings.  Example: "12 Feb 1845, 1880-95, 1899-1920, June 1950+" becomes "1845 1880-95 1899-1920 1950+".
 */
@LibraryRegister(category="transformers")
public class YearRangeExtractorTransformer implements Transformer {

	String regex = "\\b(1[6789]\\d\\d|20[012]\\d)([–—-]\\b|\\b\\+|\\b)";

	@Override
	public String transform(String s) {
		StringBuffer sb = new StringBuffer();
		if (s != null) {
			Pattern patt = Pattern.compile(regex);
			Matcher m = patt.matcher(s);
			while (m.find()) {
				String match = m.group();
				match = match.replace('–', '-'); // en-dash
				match = match.replace('—', '-'); // em-dash
				sb.append(match);
				if (!match.endsWith("-")) {
					sb.append(" ");
				}
			}
		}
		return sb.toString().trim();
	}
}
