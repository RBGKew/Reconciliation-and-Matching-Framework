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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.kew.rmf.refine.domain.query.Property;

public class CitationToPropertiesConverterTest {

	@Test
	public void testExtractProperties() {
		QueryStringToPropertiesExtractor extractor = new CitationToPropertiesConverter();

		Property expectedPublication = new Property();
		Property expectedDate = new Property();
		Property expectedAuthors = new Property();

		expectedPublication.setP("publication");
		expectedPublication.setPid(expectedPublication.getP());
		expectedAuthors.setP("authors");
		expectedAuthors.setPid(expectedAuthors.getP());
		expectedDate.setP("date");
		expectedDate.setPid(expectedDate.getP());

		// Publication
		expectedPublication.setV("Reconciliation Service");
		assertThat(asList(extractor.extractProperties("Reconciliation Service")), hasItems(expectedPublication));
		assertThat(asList(extractor.extractProperties("Reconciliation Service")).size(), equalTo(1));

		// Publication and authors
		expectedPublication.setV("Reconciliation Service");
		expectedAuthors.setV("Matthew Blissett et. al.");
		assertThat(asList(extractor.extractProperties("Reconciliation Service [Matthew Blissett et. al.]")), hasItems(expectedPublication, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Reconciliation Service [Matthew Blissett et. al.]")).size(), equalTo(2));

		// Publication and date
		expectedPublication.setV("Reconciliation Service");
		expectedDate.setV("2014");
		assertThat(asList(extractor.extractProperties("Reconciliation Service, (2014).")), hasItems(expectedPublication, expectedDate));
		assertThat(asList(extractor.extractProperties("Reconciliation Service, (2014).")).size(), equalTo(2));

		// All three
		expectedPublication.setV("Reconciliation Service");
		expectedAuthors.setV("Matthew Blissett et. al.");
		expectedDate.setV("2014");
		assertThat(asList(extractor.extractProperties("Reconciliation Service [Matthew Blissett et. al.], (2014).")), hasItems(expectedPublication, expectedDate, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Reconciliation Service [Matthew Blissett et. al.], (2014).")).size(), equalTo(3));
	}
}
