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

public class GenusSpeciesInfraspeciesToPropertiesConverterTest {

	@Test
	public void testExtractProperties() {
		QueryStringToPropertiesExtractor extractor = new GenusSpeciesInfraspeciesToPropertiesConverter();

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

		// Asteraceae subfam. Barnadesioidae K.Bremer & R.K.Jansen
		//expectedGenus.setV("Barnadesioidae");
		//expectedAuthors.setV("K.Bremer & R.K.Jansen");
		//assertThat(asList(extractor.extractProperties("Asteraceae subfam. Barnadesioidae K.Bremer & R.K.Jansen")), hasItems(expectedGenus, expectedAuthors));
		//assertThat(asList(extractor.extractProperties("Asteraceae subfam. Barnadesioidae K.Bremer & R.K.Jansen")).size(), equalTo(2));

		// Barnadesioidae K.Bremer & R.K.Jansen
		expectedGenus.setV("Barnadesioidae");
		expectedAuthors.setV("K.Bremer & R.K.Jansen");
		assertThat(asList(extractor.extractProperties("Barnadesioidae K.Bremer & R.K.Jansen")), hasItems(expectedGenus, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Barnadesioidae K.Bremer & R.K.Jansen")).size(), equalTo(2));

		// Asteraceae trib. Chaenactideae B.G.Baldwin
		//expectedGenus.setV("Chaenactideae");
		//expectedAuthors.setV("B.G.Baldwin");
		//assertThat(asList(extractor.extractProperties("Asteraceae trib. Chaenactideae B.G.Baldwin")), hasItems(expectedGenus, expectedAuthors));
		//assertThat(asList(extractor.extractProperties("Asteraceae trib. Chaenactideae B.G.Baldwin")).size(), equalTo(2));

		// Asteraceae subtrib. Chaenactidinae Rydb.
		expectedGenus.setV("Chaenactideae");
		expectedAuthors.setV("B.G.Baldwin");
		assertThat(asList(extractor.extractProperties("Chaenactideae B.G.Baldwin")), hasItems(expectedGenus, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Chaenactideae B.G.Baldwin")).size(), equalTo(2));

		// Asteraceae supersubtrib. Chaenactidodinae C.Jeffrey
		//expectedGenus.setV("Chaenactidodinae");
		//expectedAuthors.setV("C.Jeffrey");
		//assertThat(asList(extractor.extractProperties("Asteraceae supersubtrib. Chaenactidodinae C.Jeffrey")), hasItems(expectedGenus, expectedAuthors));
		//assertThat(asList(extractor.extractProperties("Asteraceae supersubtrib. Chaenactidodinae C.Jeffrey")).size(), equalTo(2));

		// Quercus
		expectedGenus.setV("Quercus");
		assertThat(asList(extractor.extractProperties("Quercus")), hasItems(expectedGenus));
		assertThat(asList(extractor.extractProperties("Quercus")).size(), equalTo(1));

		// Quercus L.
		expectedGenus.setV("Quercus");
		expectedAuthors.setV("L.");
		assertThat(asList(extractor.extractProperties("Quercus L.")), hasItems(expectedGenus, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Quercus L.")).size(), equalTo(2));

		// Helichrysum sect. Chrysocephalum (Walp.) Benth.
		//expectedGenus.setV("Helichrysum");
		//expectedSpecies.setV("Chrysocephalum");
		//expectedAuthors.setV("(Walp.) Benth.");
		//assertThat(asList(extractor.extractProperties("Helichrysum sect. Chrysocephalum (Walp.) Benth.")), hasItems(expectedGenus, expectedSpecies, expectedAuthors));
		//assertThat(asList(extractor.extractProperties("Helichrysum sect. Chrysocephalum (Walp.) Benth.")).size(), equalTo(3));

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

		// Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		//expectedGenus.setV("Quercus");
		//expectedSpecies.setV("alba");
		//expectedInfraspecies.setV("latiloba");
		//expectedAuthors.setV("(Sarg.) E.J.Palmer & Steyerm.");
		//assertThat(asList(extractor.extractProperties("Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.")), hasItems(expectedGenus, expectedSpecies, expectedInfraspecies, expectedAuthors));
		//assertThat(asList(extractor.extractProperties("Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.")).size(), equalTo(4));

		// • Less-perfect examples

		// Jaffueliobryum arsenei  (Thér.) Thér.
		// (Multiple spaces.)
		expectedGenus.setV("Jaffueliobryum");
		expectedSpecies.setV("arsenei");
		expectedAuthors.setV("(Thér.) Thér.");
		assertThat(asList(extractor.extractProperties("Jaffueliobryum arsenei  (Thér.) Thér.")), hasItems(expectedGenus, expectedSpecies, expectedAuthors));
		assertThat(asList(extractor.extractProperties("Jaffueliobryum arsenei  (Thér.) Thér.")).size(), equalTo(3));
	}
}
