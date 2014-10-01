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
package org.kew.rmf.reconciliation.queryextractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.kew.rmf.refine.domain.query.Property;

import com.google.common.base.Strings;

/**
 * Extracts the publication, date and authors from a full reference.
 */
public class CitationToPropertiesConverter implements QueryStringToPropertiesExtractor {

	Pattern pattern = Pattern.compile("^([^\\[,(]*)(\\[(.*)\\]|)(, \\((\\d*)\\)|)");

	@Override
	public Property[] extractProperties(String queryString) {
		/*
		 * Expected format:
		 * Publication abbreviation [Authors], (YEAR).
		 */
		queryString.replace("  ", " ");
		String publication = null, authors = null, date = null;

		if (queryString != null) {
			java.util.regex.Matcher m = pattern.matcher(queryString);
			if (m.find()) {
				publication = m.group(1).trim();
				authors = Strings.emptyToNull(m.group(3));
				date = Strings.emptyToNull(m.group(5));
			}
		}

		return makeProperties(publication, authors, date);
	}

	private Property[] makeProperties(String publication, String authors, String date) {
		List<org.kew.rmf.refine.domain.query.Property> properties = new ArrayList<>();

		if (publication != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("publication");
			p.setPid("publication");
			p.setV(publication);
			properties.add(p);
		}

		if (authors != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("authors");
			p.setPid("authors");
			p.setV(authors);
			properties.add(p);
		}

		if (date != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("date");
			p.setPid("date");
			p.setV(date);
			properties.add(p);
		}

		return properties.toArray(new Property[properties.size()]);
	}
}
