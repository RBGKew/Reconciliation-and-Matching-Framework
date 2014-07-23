package org.kew.reconciliation.queryextractor;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.kew.reconciliation.refine.domain.query.Property;

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
