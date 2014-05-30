package org.kew.stringmod.lib.transformers;

import java.util.HashMap;
import java.util.Map;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer splits a string into a series of words. The word delimiter is any 
 * sequence of non-alphanumeric characters. It then iterates over the "words" and 
 * converts any Roman numerals to their Arabic equivalent, then concatenates these 
 * converted words back into a single string, using the space character to separate words.
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class RomanNumeralTransformer implements Transformer{

	private static Map<String,String> map = new HashMap<String,String>();
	private final static String[] BASIC_ROMAN_NUMBERS = { "M", "CM", "D", "CD",
        "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
	private final static int[] BASIC_VALUES = { 1000, 900, 500, 400, 100, 90,
        50, 40, 10, 9, 5, 4, 1 };

	static{
		for (int i = 1; i <= 5000; i++){
			map.put(toRomanValue(i), Integer.toString(i));
		}
	}

	public static String toRomanValue(int arabicValue) {
		StringBuffer sb = new StringBuffer();
		int remainder = arabicValue;
		for (int i = 0; i < BASIC_VALUES.length; i++) {
			while (remainder >= BASIC_VALUES[i]) {
				sb.append(BASIC_ROMAN_NUMBERS[i]);
				remainder -= BASIC_VALUES[i];
			}
		}
		return sb.toString();
	}

	@Override
	public String transform(String s) {
		String[] words = s.replaceAll("[^A-Za-z0-9]", " ").replaceAll("\\s+", " ").split(" ");
		String[] converted_words = new String[words.length];
		for (int i = 0; i < words.length; i++) {
			String roman = map.get(words[i].toUpperCase());
			if (roman != null)
				converted_words[i] = roman;
			else
				converted_words[i] = words[i];
		}
		StringBuffer sb = new StringBuffer();
		for (String converted_word : converted_words){
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(converted_word);
		}
		return sb.toString();
	}

}