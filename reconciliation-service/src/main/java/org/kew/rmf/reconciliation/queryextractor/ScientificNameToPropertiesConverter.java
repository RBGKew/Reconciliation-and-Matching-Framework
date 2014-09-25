package org.kew.rmf.reconciliation.queryextractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.kew.rmf.refine.domain.query.Property;
import org.kew.rmf.transformers.authors.StripBasionymAuthorTransformer;
import org.kew.rmf.transformers.authors.StripPublishingAuthorTransformer;
import org.perf4j.LoggingStopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a scientific name string — of any reasonable rank — into
 * a first, second and third epithet, plus authors, using the GBIF {@link NameParser}.
 */
public class ScientificNameToPropertiesConverter implements QueryStringToPropertiesExtractor {
	private static final Logger logger = LoggerFactory.getLogger(ScientificNameToPropertiesConverter.class);

	private NameParser gbifNameParser = new NameParser();

	private StripPublishingAuthorTransformer stripPublishingAuthor = new StripPublishingAuthorTransformer();
	private StripBasionymAuthorTransformer stripBasionymAuthors = new StripBasionymAuthorTransformer();

	@Override
	public Property[] extractProperties(String queryString) {
		// Log the query if it takes more than 250ms — there are suggestions the GBIF Name Parser may be slow in some cases.
		LoggingStopWatch speedCheck = new Slf4JStopWatch("NameParsing:"+queryString, logger, Slf4JStopWatch.WARN_LEVEL).setTimeThreshold(250);

		queryString = stripUnwantedFamilyEpithet(queryString);

		try {
			String epithet1, epithet2, epithet3, basionymAuthors, publishingAuthors;
			ParsedName parsedName = gbifNameParser.parse(queryString);

			/*
			 * May return combinations of genusOrAbove, infraGeneric, infraSpecific, specific.
			 */

			// genusOrAbove should always be present
			epithet1 = parsedName.getGenusOrAbove();

			// if infraGeneric is present it's the second epithet and the rank will be infrageneric
			if (parsedName.getInfraGeneric() != null) {
				epithet2 = parsedName.getInfraGeneric();
				epithet3 = null;
			}
			// otherwise we may have species epithets
			else {
				epithet2 = parsedName.getSpecificEpithet();
				epithet3 = parsedName.getInfraSpecificEpithet();
			}

			basionymAuthors = parsedName.getBracketAuthorship();
			publishingAuthors = parsedName.getAuthorship();

			//parsedName.getNomStatus();
			//parsedName.getRank();
			//parsedName.getRankMarker();

			speedCheck.stop();
			return makeProperties(epithet1, epithet2, epithet3, basionymAuthors, publishingAuthors);
		}
		catch (UnparsableException e) {
			logger.info("GBIF NameParser couldn't handle «"+queryString+"»", e);
			// Have a go at the fallback parsing
			return fallbackParsing(queryString);
		}
	}

	private Pattern unwantedFamilyEpithet = Pattern.compile(
			"^" +
			/*
			 * ICBN Art 18.1:
			 * The name of a family is a plural adjective used as a noun; … termination -aceae
			 */
			"(\\p{L}+aceae" +
			/*
			 * ICBN Art 18.4:
			 * When a name of a family has been published with an improper Latin termination…
			 */
			/*
			 * ICBN Art. 18.5:
			 * <blockquote>
			 * 	The following names, of long usage, are treated as validly published:
			 * 	Palmae (Arecaceae; type, Areca L.);
			 * 	Gramineae (Poaceae; type, Poa L.);
			 * 	Cruciferae (Brassicaceae; type, Brassica L.);
			 * 	Leguminosae (Fabaceae; type, Faba Mill. [= Vicia L.]);
			 * 	Guttiferae (Clusiaceae; type, Clusia L.);
			 * 	Umbelliferae (Apiaceae; type, Apium L.);
			 * 	Labiatae (Lamiaceae; type, Lamium L.);
			 * 	Compositae (Asteraceae; type, Aster L.).
			 * 	When the Papilionaceae (Fabaceae; type, Faba Mill.) are regarded as a family distinct from the remainder of the
			 * 	Leguminosae, the name Papilionaceae is conserved against Leguminosae.
			 * </blockquote>
			 */
			"|Palmae" +
			"|Gramineae" +
			"|Cruciferae" +
			"|Leguminosae" +
			"|Guttiferae" +
			"|Umbelliferae" +
			"|Labiatae" +
			"|Compositae)" +
			/*
			 * Followed by an infrafamilial rank (subfam., supsubtrib. etc)
			 */
			"\\s+[a-z]+\\.");

	/**
	 * Removes an initial family + infrafamilial rank from a string, so the GBIF name parser
	 * returns the rest of the name, not just a family.
	 */
	private String stripUnwantedFamilyEpithet(String s) {
		if (s == null) return null;

		return unwantedFamilyEpithet.matcher(s).replaceFirst("");
	}

	/**
	 * Fallback parsing, in case the GBIF parser fails.
	 */
	private Property[] fallbackParsing(String queryString) {
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
		String basionymAuthors = stripPublishingAuthor.transform(authors);
		String publishingAuthors = stripBasionymAuthors.transform(authors);
		return makeProperties(genus, species, infraspecies, basionymAuthors, publishingAuthors);
	}

	private Property[] makeProperties(String genus, String species, String infraspecies, String basionymAuthors, String publishingAuthors) {
		List<org.kew.rmf.refine.domain.query.Property> properties = new ArrayList<>();

		if (genus != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("epithet_1");
			p.setPid("epithet_1");
			p.setV(genus);
			properties.add(p);
		}

		if (species != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("epithet_2");
			p.setPid("epithet_2");
			p.setV(species);
			properties.add(p);
		}

		if (infraspecies != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("epithet_3");
			p.setPid("epithet_3");
			p.setV(infraspecies);
			properties.add(p);
		}

		if (basionymAuthors != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("basionym_author");
			p.setPid("basionym_author");
			p.setV(basionymAuthors);
			properties.add(p);
		}

		if (publishingAuthors != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("publishing_author");
			p.setPid("publishing_author");
			p.setV(publishingAuthors);
			properties.add(p);
		}

		return properties.toArray(new Property[properties.size()]);
	}
}
