/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.matchers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * This matcher tests for common tokens after splitting the input strings into a sequence of ngrams. 
 * The ngram length ("n") is configurable.
 * @author nn00kg
 *
 */
public class NGramMatcher extends CommonTokensMatcher{

	private int nGramLength;
	public static int COST = 10;

	@Override
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

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
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
