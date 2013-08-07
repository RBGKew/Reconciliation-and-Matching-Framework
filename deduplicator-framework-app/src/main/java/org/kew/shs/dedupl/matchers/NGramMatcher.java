package org.kew.shs.dedupl.matchers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.util.LibraryRegister;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens after splitting the input strings into a sequence of ngrams. 
 * The ngram length ("n") is configurable.
 * @author nn00kg
 *
 */
@LibraryRegister(category="matchers")
public class NGramMatcher extends CommonTokensMatcher{

	private int nGramLength;
	public static int COST = 10;
	
	@Cacheable(cacheName="ngMatchCache")
	public boolean matches(String s1, String s2){
		boolean matches = false;
		if (StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2)){
			matches = s1.equals(s2); 
			if (!matches){
				String[] a1 = extract(s1, nGramLength);
				String[] a2 = extract(s2, nGramLength);
				matches = calculateTokensInCommon(a1, a2);
			}
		}
		return matches;
	}
	
	public static String[] extract(String input, int n){
		List<String> ngramlist = new ArrayList<String>();;
		if (input != null){
			input = "_" + input + "_";
			if (input.length() > n){
				for (int i = 0; i < input.length() - n; i++){
					ngramlist.add(input.substring(i, i+n));
				}
			}
			else{
				ngramlist.add(input);
			}
		}
		String[] ngrams = new String[ngramlist.size()];
		return (String[])ngramlist.toArray(ngrams);
	}

	public int getnGramLength() {
		return nGramLength;
	}

	public void setnGramLength(int nGramLength) {
		this.nGramLength = nGramLength;
	}
	
	public boolean isExact() {
		return false;
	}
	
	public String getExecutionReport() {
		return null;
	}
	
	public static void main(String[] args) {
		NGramMatcher nGramMatcher = new NGramMatcher();
		nGramMatcher.setMinRatio(0.5);
		nGramMatcher.setnGramLength(2);
		System.out.println(nGramMatcher.matches("lofgreniana", "loefgreniana"));
	}
	
}
