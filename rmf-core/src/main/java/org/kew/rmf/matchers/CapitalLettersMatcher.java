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

import org.kew.rmf.transformers.CapitalLettersExtractor;

/**
 * This matcher tests for common tokens using only the capital letters in the strings supplied.
 */
public class CapitalLettersMatcher extends CommonTokensMatcher{
	
	CapitalLettersExtractor removeNonCaps = new CapitalLettersExtractor();

	@Override
	public boolean matches(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		this.removeNonCaps.setReplacement(this.getDelimiter());
		return super.matches(this.removeNonCaps.transform(s1), this.removeNonCaps.transform(s2));
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}

	public static void main(String[] args) throws Exception {
		CapitalLettersMatcher m = new CapitalLettersMatcher();
		System.out.println(m.matches("Addit. Fl. Jam.", "Bull. Inst. Jamaica, Sci. Ser."));
		CommonTokensMatcher cm = new CommonTokensMatcher();
		System.out.println(cm.matches("A F J", "B I J S S"));
	}

}
