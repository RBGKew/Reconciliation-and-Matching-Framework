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
