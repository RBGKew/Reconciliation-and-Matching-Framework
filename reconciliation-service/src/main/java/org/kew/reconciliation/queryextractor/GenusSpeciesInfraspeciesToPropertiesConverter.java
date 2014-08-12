package org.kew.reconciliation.queryextractor;

import java.util.ArrayList;
import java.util.List;

import org.kew.reconciliation.refine.domain.query.Property;

public class GenusSpeciesInfraspeciesToPropertiesConverter implements QueryStringToPropertiesExtractor {

	@Override
	public Property[] extractProperties(String queryString) {
		/*
		 * Example scientific names:
		 * Fagaceae
		 * Fagaceae Juss.
		 * Quercus
		 * Quercus L.
		 * Quercus alba
		 * Quercus alba L.
		 * Quercus alba f. alba
		 * Quercus alba f. viridis Trel.
		 * Quercus alba f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		 * Quercus alba L. f. latiloba (Sarg.) E.J.Palmer & Steyerm.
		 *
		 * For the moment, this is just a very basic implementation to satisfy the requirements of the
		 * Reconciliation Service "suggest" and "preview" queries.
		 */
		queryString.replace("  ", " ");
		String[] parts = queryString.split(" ");
		String genus = null, species = null, infraspecies = null, authors = null;

		// Assume first part is generic epithet
		genus = parts[0];

		if (parts.length == 1) return makeProperties(genus, species, infraspecies, authors);

		// If second part has capital letter, assume rest of name is authors
		if (isCapital(parts[1].charAt(0))) {
			authors = joinRest(parts, 1);
		}
		// Otherwise, assume second part is species
		else {
			species = parts[1];

			if (parts.length == 2) return makeProperties(genus, species, infraspecies, authors);

			// If third part has capital letter, assume rest of name is authors
			if (isCapital(parts[2].charAt(0))) {
				authors = joinRest(parts, 2);
			}
			// Otherwise, infraspecific rank (ignore) and infraspecific epithet
			else {
				// parts[2] is infraspecific rank

				if (parts.length == 3) return makeProperties(genus, species, infraspecies, authors);

				infraspecies = parts[3]; // bounds check

				if (parts.length == 4) return makeProperties(genus, species, infraspecies, authors);

				authors = joinRest(parts, 4);
			}
		}

		return makeProperties(genus, species, infraspecies, authors);
	}

	private boolean isCapital(char letter) {
		return letter >= 'A' && letter <= 'Z';
	}

	/**
	 * Joins from index'th to last element of array with space.
	 *
	 * If index is out of bounds, returns empty string.
	 */
	private String joinRest(String[] array, int index) {
		StringBuilder joined = new StringBuilder();
		while (index < array.length) {
			if (joined.length() > 0) joined.append(' ');
			joined.append(array[index]);
			index++;
		}
		return joined.toString();
	}

	private Property[] makeProperties(String genus, String species, String infraspecies, String authors) {
		List<org.kew.reconciliation.refine.domain.query.Property> properties = new ArrayList<>();

		if (genus != null) {
			org.kew.reconciliation.refine.domain.query.Property p = new org.kew.reconciliation.refine.domain.query.Property();
			p.setP("genus");
			p.setPid("genus");
			p.setV(genus);
			properties.add(p);
		}

		if (species != null) {
			org.kew.reconciliation.refine.domain.query.Property p = new org.kew.reconciliation.refine.domain.query.Property();
			p.setP("species");
			p.setPid("species");
			p.setV(species);
			properties.add(p);
		}

		if (infraspecies != null) {
			org.kew.reconciliation.refine.domain.query.Property p = new org.kew.reconciliation.refine.domain.query.Property();
			p.setP("infraspecies");
			p.setPid("infraspecies");
			p.setV(infraspecies);
			properties.add(p);
		}

		if (authors != null) {
			org.kew.reconciliation.refine.domain.query.Property p = new org.kew.reconciliation.refine.domain.query.Property();
			p.setP("authors");
			p.setPid("authors");
			p.setV(authors);
			properties.add(p);
		}

		return properties.toArray(new Property[properties.size()]);
	}
}
