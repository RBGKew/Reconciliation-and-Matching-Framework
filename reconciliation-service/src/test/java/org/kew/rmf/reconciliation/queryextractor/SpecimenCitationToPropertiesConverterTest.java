/*
 * Reconciliation and Matching Fram!ework
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

import org.junit.Ignore;
import org.junit.Test;
import org.kew.rmf.refine.domain.query.Property;

public class SpecimenCitationToPropertiesConverterTest {

	@Test @Ignore
	public void testExtractProperties() {
		QueryStringToPropertiesExtractor extractor = new SpecimenCitationToPropertiesConverter();

		Property expectedCollectorName = new Property();
		Property expectedCollectorNumber = new Property();
		Property expectedCollectionDate = new Property();
		Property expectedLocality = new Property();
		Property expectedCountry = new Property();
		Property expectedHerbarium = new Property();

		expectedCollectorName.setP("collector_name");
		expectedCollectorName.setPid("collector_name");
		expectedCollectorNumber.setP("collector_number");
		expectedCollectorNumber.setPid("collector_number");
		expectedCollectionDate.setP("collection_date");
		expectedCollectionDate.setPid("collection_date");
		expectedLocality.setP("locality");
		expectedLocality.setPid("locality");
		expectedCountry.setP("country");
		expectedCountry.setPid("country");
		expectedHerbarium.setP("herbarium");
		expectedHerbarium.setPid("herbarium");

		// Zambia: c. 3 km west of Kalabo, foot of escarpment at edge of swamp bordering river, fl. 13.xi.1959, Drummond & Cookson 6442 (E; K; LISC; SRGH).
		String citation = "Zambia: c. 3 km west of Kalabo, foot of escarpment at edge of swamp bordering river, fl. 13.xi.1959, Drummond & Cookson 6442 (E; K; LISC; SRGH).";
		expectedCountry.setV("Zambia");
		expectedLocality.setV("c. 3 km west of Kalabo, foot of escarpment at edge of swamp bordering river, fl.");
		expectedCollectionDate.setV("13.xi.1959");
		expectedCollectorName.setV("Drummond & Cookson");
		expectedCollectorNumber.setV("6442");
		expectedHerbarium.setV("E; K; LISC; SRGH");
		assertThat(asList(extractor.extractProperties(citation)), hasItems(expectedCollectorName, expectedCollectorNumber, expectedCollectionDate, expectedLocality, expectedCountry, expectedHerbarium));
		assertThat(asList(extractor.extractProperties(citation)).size(), equalTo(6));
	}
}
