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

		Property expectedEpithet1 = new Property();
		Property expectedEpithet2 = new Property();
		Property expectedEpithet3 = new Property();
		Property expectedBasionymAuthor = new Property();
		Property expectedPublishingAuthor = new Property();

		expectedEpithet1.setP("epithet_1");
		expectedEpithet1.setPid("epithet_1");
		expectedEpithet2.setP("epithet_2");
		expectedEpithet2.setPid("epithet_2");
		expectedEpithet3.setP("epithet_3");
		expectedEpithet3.setPid("epithet_3");
		expectedBasionymAuthor.setP("basionym_author");
		expectedBasionymAuthor.setPid("basionym_author");
		expectedPublishingAuthor.setP("publishing_author");
		expectedPublishingAuthor.setPid("publishing_author");

		// Fagaceae
		expectedEpithet1.setV("Fagaceae");
		assertThat(asList(extractor.extractProperties("Fagaceae")), hasItems(expectedEpithet1));
		assertThat(asList(extractor.extractProperties("Fagaceae")).size(), equalTo(1));

		// Fagaceae Juss.
		expectedEpithet1.setV("Fagaceae");
		expectedPublishingAuthor.setV("Juss.");
		assertThat(asList(extractor.extractProperties("Fagaceae Juss.")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Fagaceae Juss.")).size(), equalTo(2));

		// Asteraceae subfam. Barnadesioidae K.Bremer & R.K.Jansen
		expectedEpithet1.setV("Barnadesioidae");
		expectedPublishingAuthor.setV("K.Bremer & R.K.Jansen");
		assertThat(asList(extractor.extractProperties("Asteraceae subfam. Barnadesioidae K.Bremer & R.K.Jansen")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Asteraceae subfam. Barnadesioidae K.Bremer & R.K.Jansen")).size(), equalTo(2));

		// Barnadesioidae K.Bremer & R.K.Jansen
		expectedEpithet1.setV("Barnadesioidae");
		expectedPublishingAuthor.setV("K.Bremer & R.K.Jansen");
		assertThat(asList(extractor.extractProperties("Barnadesioidae K.Bremer & R.K.Jansen")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Barnadesioidae K.Bremer & R.K.Jansen")).size(), equalTo(2));

		// Asteraceae trib. Chaenactideae B.G.Baldwin
		expectedEpithet1.setV("Chaenactideae");
		expectedPublishingAuthor.setV("B.G.Baldwin");
		assertThat(asList(extractor.extractProperties("Asteraceae trib. Chaenactideae B.G.Baldwin")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Asteraceae trib. Chaenactideae B.G.Baldwin")).size(), equalTo(2));

		// Asteraceae subtrib. Chaenactidinae Rydb.
		expectedEpithet1.setV("Chaenactideae");
		expectedPublishingAuthor.setV("B.G.Baldwin");
		assertThat(asList(extractor.extractProperties("Chaenactideae B.G.Baldwin")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Chaenactideae B.G.Baldwin")).size(), equalTo(2));

		// Asteraceae supersubtrib. Chaenactidodinae C.Jeffrey
		expectedEpithet1.setV("Chaenactidodinae");
		expectedPublishingAuthor.setV("C.Jeffrey");
		assertThat(asList(extractor.extractProperties("Asteraceae supersubtrib. Chaenactidodinae C.Jeffrey")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Asteraceae supersubtrib. Chaenactidodinae C.Jeffrey")).size(), equalTo(2));

		// Quercus
		expectedEpithet1.setV("Quercus");
		assertThat(asList(extractor.extractProperties("Quercus")), hasItems(expectedEpithet1));
		assertThat(asList(extractor.extractProperties("Quercus")).size(), equalTo(1));

		// Quercus L.
		expectedEpithet1.setV("Quercus");
		expectedPublishingAuthor.setV("L.");
		assertThat(asList(extractor.extractProperties("Quercus L.")), hasItems(expectedEpithet1, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Quercus L.")).size(), equalTo(2));

		// Helichrysum sect. Chrysocephalum (Walp.) Benth.
		expectedEpithet1.setV("Helichrysum");
		expectedEpithet2.setV("Chrysocephalum");
		expectedBasionymAuthor.setV("Walp.");
		expectedPublishingAuthor.setV("Benth.");
		assertThat(asList(extractor.extractProperties("Helichrysum sect. Chrysocephalum (Walp.) Benth.")), hasItems(expectedEpithet1, expectedEpithet2, expectedBasionymAuthor, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Helichrysum sect. Chrysocephalum (Walp.) Benth.")).size(), equalTo(4));

		// Quercus alba
		expectedEpithet1.setV("Quercus");
		expectedEpithet2.setV("alba");
		assertThat(asList(extractor.extractProperties("Quercus alba")), hasItems(expectedEpithet1, expectedEpithet2));
		assertThat(asList(extractor.extractProperties("Quercus alba")).size(), equalTo(2));

		// Quercus alba L.
		expectedEpithet1.setV("Quercus");
		expectedEpithet2.setV("alba");
		expectedPublishingAuthor.setV("L.");
		assertThat(asList(extractor.extractProperties("Quercus alba L.")), hasItems(expectedEpithet1, expectedEpithet2, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Quercus alba L.")).size(), equalTo(3));

		// Quercus alba f. alba
		expectedEpithet1.setV("Quercus");
		expectedEpithet2.setV("alba");
		expectedEpithet3.setV("alba");
		assertThat(asList(extractor.extractProperties("Quercus alba f. alba")), hasItems(expectedEpithet1, expectedEpithet2, expectedEpithet3));
		assertThat(asList(extractor.extractProperties("Quercus alba f. alba")).size(), equalTo(3));

		// Quercus alba f. viridis Trel.
		expectedEpithet1.setV("Quercus");
		expectedEpithet2.setV("alba");
		expectedEpithet3.setV("viridis");
		expectedPublishingAuthor.setV("Trel.");
		assertThat(asList(extractor.extractProperties("Quercus alba f. viridis Trel.")), hasItems(expectedEpithet1, expectedEpithet2, expectedEpithet3, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Quercus alba f. viridis Trel.")).size(), equalTo(4));

		// Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		expectedEpithet1.setV("Quercus");
		expectedEpithet2.setV("alba");
		expectedEpithet3.setV("latiloba");
		expectedBasionymAuthor.setV("Sarg.");
		expectedPublishingAuthor.setV("E.J.Palmer & Steyerm.");
		assertThat(asList(extractor.extractProperties("Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.")), hasItems(expectedEpithet1, expectedEpithet2, expectedEpithet3, expectedBasionymAuthor, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.")).size(), equalTo(5));

		// Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		expectedEpithet1.setV("Quercus");
		expectedEpithet2.setV("alba");
		expectedEpithet3.setV("latiloba");
		expectedBasionymAuthor.setV("Sarg.");
		expectedPublishingAuthor.setV("E.J.Palmer & Steyerm.");
		assertThat(asList(extractor.extractProperties("Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.")), hasItems(expectedEpithet1, expectedEpithet2, expectedEpithet3, expectedBasionymAuthor, expectedPublishingAuthor));
		assertThat(asList(extractor.extractProperties("Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.")).size(), equalTo(5));
	}
}
