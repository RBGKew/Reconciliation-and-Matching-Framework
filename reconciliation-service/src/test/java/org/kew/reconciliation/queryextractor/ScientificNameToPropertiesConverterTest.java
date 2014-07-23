package org.kew.reconciliation.queryextractor;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.kew.reconciliation.refine.domain.query.Property;

public class ScientificNameToPropertiesConverterTest {

	@Test
	public void testExtractProperties() {
		QueryStringToPropertiesExtractor extractor = new ScientificNameToPropertiesConverter();

		Property expectedGenus = new Property();
		Property expectedSpecies = new Property();
		Property expectedInfraspecies = new Property();
		Property expectedAuthors = new Property();

		expectedGenus.setP("genus");
		expectedGenus.setPid("genus");
		expectedSpecies.setP("species");
		expectedSpecies.setPid("species");
		expectedInfraspecies.setP("infraspecies");
		expectedInfraspecies.setPid("infraspecies");
		expectedAuthors.setP("authors");
		expectedAuthors.setPid("authors");

		// Fagaceae
		expectedGenus.setV("Fagaceae");
		assertThat(asList(extractor.extractProperties("Fagaceae")), hasItems(expectedGenus));
		assertThat(asList(extractor.extractProperties("Fagaceae")).size(), equalTo(1));

		// Fagaceae Juss.
		expectedGenus.setV("Fagaceae");
		expectedAuthors.setV("Juss.");
		assertThat(asList(extractor.extractProperties("Fagaceae Juss.")), hasItems(expectedGenus, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Fagaceae Juss.")).size(), equalTo(2));

		// Quercus
		expectedGenus.setV("Quercus");
		assertThat(asList(extractor.extractProperties("Quercus")), hasItems(expectedGenus));
		assertThat(asList(extractor.extractProperties("Quercus")).size(), equalTo(1));

		// Quercus L.
		expectedGenus.setV("Quercus");
		expectedAuthors.setV("L.");
		assertThat(asList(extractor.extractProperties("Quercus L.")), hasItems(expectedGenus, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Quercus L.")).size(), equalTo(2));

		// Quercus alba
		expectedGenus.setV("Quercus");
		expectedSpecies.setV("alba");
		assertThat(asList(extractor.extractProperties("Quercus alba")), hasItems(expectedGenus, expectedSpecies));
		assertThat(asList(extractor.extractProperties("Quercus alba")).size(), equalTo(2));

		// Quercus alba L.
		expectedGenus.setV("Quercus");
		expectedSpecies.setV("alba");
		expectedAuthors.setV("L.");
		assertThat(asList(extractor.extractProperties("Quercus alba L.")), hasItems(expectedGenus, expectedSpecies, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Quercus alba L.")).size(), equalTo(3));

		// Quercus alba f. alba
		expectedGenus.setV("Quercus");
		expectedSpecies.setV("alba");
		expectedInfraspecies.setV("alba");
		assertThat(asList(extractor.extractProperties("Quercus alba f. alba")), hasItems(expectedGenus, expectedSpecies, expectedInfraspecies));
		assertThat(asList(extractor.extractProperties("Quercus alba f. alba")).size(), equalTo(3));

		// Quercus alba f. viridis Trel.
		expectedGenus.setV("Quercus");
		expectedSpecies.setV("alba");
		expectedInfraspecies.setV("viridis");
		expectedAuthors.setV("Trel.");
		assertThat(asList(extractor.extractProperties("Quercus alba f. viridis Trel.")), hasItems(expectedGenus, expectedSpecies, expectedInfraspecies, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Quercus alba f. viridis Trel.")).size(), equalTo(4));

		// Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		expectedGenus.setV("Quercus");
		expectedSpecies.setV("alba");
		expectedInfraspecies.setV("latiloba");
		expectedAuthors.setV("(Sarg.) E.J.Palmer & Steyerm.");
		assertThat(asList(extractor.extractProperties("Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.")), hasItems(expectedGenus, expectedSpecies, expectedInfraspecies, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.")).size(), equalTo(4));

		// TODO: Not yet implemented.
		// Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		//expectedGenus.setV("Quercus");
		//expectedSpecies.setV("alba");
		//expectedInfraspecies.setV("latiloba");
		//expectedAuthors.setV("(Sarg.) E.J.Palmer & Steyerm.");
		//assertThat(asList(extractor.extractProperties("Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.")), hasItems(expectedGenus, expectedSpecies, expectedInfraspecies, expectedAuthors));
		//assertThat(asList(extractor.extractProperties("Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.")).size(), equalTo(4));
	}
}
