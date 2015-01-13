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
package org.kew.rmf.core.lucene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kew.rmf.core.configuration.Configuration;
import org.kew.rmf.core.configuration.Property;
import org.kew.rmf.matchers.AlwaysMatchingMatcher;
import org.kew.rmf.matchers.ExactMatcher;
import org.kew.rmf.matchers.MatchException;
import org.kew.rmf.transformers.StripNonAlphabeticCharactersTransformer;
import org.kew.rmf.transformers.TransformationException;
import org.kew.rmf.transformers.Transformer;
import org.kew.rmf.transformers.WeightedTransformer;
import org.kew.rmf.transformers.authors.ShrunkAuthors;
import org.kew.rmf.transformers.authors.StripBasionymAuthorTransformer;
import org.kew.rmf.transformers.botany.EpithetTransformer;
import org.kew.rmf.transformers.botany.FakeHybridSignCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneUtilsTest {
	private static Logger logger = LoggerFactory.getLogger(LuceneUtils.class);

	List<Property> genusOnly;
	List<Property> genusAndAuthor;
	List<Property> fullNameBlanksMatchOnly;
	List<Property> fullNameBlanksDifferOnly;

	@Before
	public void setUpConfigurations() {
		// Configure transformers
		Transformer fakeHybridSignCleaner = new FakeHybridSignCleaner();
		StripNonAlphabeticCharactersTransformer stripNonAlphabetic = new StripNonAlphabeticCharactersTransformer();
		stripNonAlphabetic.setReplacement("");
		Transformer epithetTransformer = new EpithetTransformer();

		Transformer stripBasionymAuthor = new StripBasionymAuthorTransformer();
		ShrunkAuthors shrunkAuthor = new ShrunkAuthors();
		shrunkAuthor.setShrinkTo(4);

		// Configure genus property
		List<Transformer> genusQueryTransformers = new ArrayList<>();
		genusQueryTransformers.add(new WeightedTransformer(fakeHybridSignCleaner, 0.1, 1));
		genusQueryTransformers.add(new WeightedTransformer(stripNonAlphabetic, 0.05, 3));
		genusQueryTransformers.add(new WeightedTransformer(epithetTransformer, 0.4, 5));

		List<Transformer> genusAuthorityTransformers = new ArrayList<>();
		genusAuthorityTransformers.add(new WeightedTransformer(stripNonAlphabetic, 0.15, 2));
		genusAuthorityTransformers.add(new WeightedTransformer(epithetTransformer, 0.3, 4));

		Property genus = new Property();
		genus.setQueryColumnName("genus");
		genus.setAuthorityColumnName("genus");
		genus.setMatcher(new ExactMatcher());
		genus.setQueryTransformers(genusQueryTransformers);
		genus.setAuthorityTransformers(genusAuthorityTransformers);

		// Configure author property
		List<Transformer> authorQueryTransformers = new ArrayList<>();
		authorQueryTransformers.add(new WeightedTransformer(stripBasionymAuthor, 0.0, -1));
		authorQueryTransformers.add(new WeightedTransformer(shrunkAuthor, 0.5, 1));

		List<Transformer> authorAuthorityTransformers = new ArrayList<>();
		authorAuthorityTransformers.add(new WeightedTransformer(stripBasionymAuthor, 0.0, -1));
		authorAuthorityTransformers.add(new WeightedTransformer(shrunkAuthor, 0.5, 1));

		Property authors = new Property();
		authors.setQueryColumnName("authors");
		authors.setAuthorityColumnName("authors");
		authors.setMatcher(new ExactMatcher());
		authors.setBlanksMatch(true);
		authors.setQueryTransformers(authorQueryTransformers);
		authors.setAuthorityTransformers(authorAuthorityTransformers);

		// Configure fullName property
		Property fullNameBlanksMatch = new Property();
		fullNameBlanksMatch.setQueryColumnName("full_name");
		fullNameBlanksMatch.setAuthorityColumnName("full_name");
		fullNameBlanksMatch.setMatcher(new AlwaysMatchingMatcher());
		fullNameBlanksMatch.setBlanksMatch(true);

		Property fullNameBlanksDiffer = new Property();
		fullNameBlanksDiffer.setQueryColumnName("full_name");
		fullNameBlanksDiffer.setAuthorityColumnName("full_name");
		fullNameBlanksDiffer.setMatcher(new AlwaysMatchingMatcher());
		fullNameBlanksDiffer.setBlanksMatch(false);

		// Set up configurations
		genusOnly = new ArrayList<>();
		genusOnly.add(genus);

		genusAndAuthor = new ArrayList<>();
		genusAndAuthor.add(genus);
		genusAndAuthor.add(authors);

		fullNameBlanksMatchOnly = new ArrayList<>();
		fullNameBlanksMatchOnly.add(fullNameBlanksMatch);

		fullNameBlanksDifferOnly = new ArrayList<>();
		fullNameBlanksDifferOnly.add(fullNameBlanksDiffer);
	}

	@Test
	public void checkSinglePropertyScores() {

		// Set up test records
		Map<String,String> queryRecord = new HashMap<>();
		Map<String,String> authorityRecord = new HashMap<>();
		queryRecord.put("id", "q");
		authorityRecord.put("id", "a");

		// Check a non-match is flagged as such, score 0.
		queryRecord.put("genus", "Ilex");
		authorityRecord.put("genus", "Quercus");
		test(false, queryRecord, authorityRecord, genusOnly);
		test(0.0, queryRecord, authorityRecord, genusOnly);

		// Check an exact match has a score of 1.
		queryRecord.put("genus", "Quercus");
		authorityRecord.put("genus", "Quercus");
		test(true, queryRecord, authorityRecord, genusOnly);
		test(1.0, queryRecord, authorityRecord, genusOnly);

		// This needs transformers up to and including strip non alphabetic, costing 0.3. Score should be 0.7.
		queryRecord.put("genus", "Quer5c5us");
		authorityRecord.put("genus", "Quer6cus");
		test(true, queryRecord, authorityRecord, genusOnly);
		test(0.7, queryRecord, authorityRecord, genusOnly);

		// This has the same score, since the fake hybrid sign cleaner comes before the strip non alphabetic transformer.
		queryRecord.put("genus", "x Quer5c5us");
		authorityRecord.put("genus", "Quer7cus");
		test(true, queryRecord, authorityRecord, genusOnly);
		test(0.7, queryRecord, authorityRecord, genusOnly);

		// This also needs the epithet transformer, which brings the required cost up to 1.0.
		queryRecord.put("genus", "x Quer5c5us");
		authorityRecord.put("genus", "Quer8ca");
		test(true, queryRecord, authorityRecord, genusOnly);
		test(0.0, queryRecord, authorityRecord, genusOnly);
	}

	@Test
	public void checkTwoPropertyMatchScores() {

		// Set up test records
		Map<String,String> queryRecord = new HashMap<>();
		Map<String,String> authorityRecord = new HashMap<>();
		queryRecord.put("id", "q");
		authorityRecord.put("id", "a");

		// Both match exactly, score is 1.0
		queryRecord.put("genus", "Quercus");
		authorityRecord.put("genus", "Quercus");
		queryRecord.put("authors", "L.");
		authorityRecord.put("authors", "L.");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(1.0, queryRecord, authorityRecord, genusAndAuthor);

		// Genus is a poor match (0.0), author is a perfect match (1), so score is 0.5.
		queryRecord.put("genus", "x Quer5cus");
		authorityRecord.put("genus", "Querca");
		queryRecord.put("authors", "L.");
		authorityRecord.put("authors", "L.");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.5, queryRecord, authorityRecord, genusAndAuthor);

		// Genus is a perfect match (1), author is a terrible match (0), so score is 0.5.
		queryRecord.put("genus", "Quercus");
		authorityRecord.put("genus", "Quercus");
		queryRecord.put("authors", "A.B.Otherelse");
		authorityRecord.put("authors", "A.B.Otherwise");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.5, queryRecord, authorityRecord, genusAndAuthor);

		// Genus is an imperfect match (0.9), author is a perfect match (1), so score is 0.95.
		queryRecord.put("genus", "x Quercus");
		authorityRecord.put("genus", "Quercus");
		queryRecord.put("authors", "A.B.Other");
		authorityRecord.put("authors", "A.B.Other");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.95, queryRecord, authorityRecord, genusAndAuthor);
	}

	@Test
	public void checkWithBlankPropertyMatchScores() {

		// Set up test records
		Map<String,String> queryRecord = new HashMap<>();
		Map<String,String> authorityRecord = new HashMap<>();
		queryRecord.put("id", "q");
		authorityRecord.put("id", "a");

		queryRecord.put("genus", "Quercus");
		authorityRecord.put("genus", "Quercus");

		// Exact author in the query
		authorityRecord.put("authors", "L.");
		queryRecord.put("authors", "L.");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(1.0, queryRecord, authorityRecord, genusAndAuthor);

		// Close author in the query
		authorityRecord.put("authors", "L.");
		queryRecord.put("authors", "l");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.5, queryRecord, authorityRecord, genusAndAuthor);

		// Blank author in the query
		authorityRecord.put("authors", "L.");
		queryRecord.put("authors", "");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.75, queryRecord, authorityRecord, genusAndAuthor);

		// Blank author in the authority
		authorityRecord.put("authors", "");
		queryRecord.put("authors", "L.");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.75, queryRecord, authorityRecord, genusAndAuthor);

		// Blank author on both sides
		queryRecord.put("authors", "");
		authorityRecord.put("authors", "");
		test(true, queryRecord, authorityRecord, genusAndAuthor);
		test(0.875, queryRecord, authorityRecord, genusAndAuthor);
	}

	@Test
	public void noDetrimentForBlankIfItMatchesAnyway() {
		// Set up test records
		Map<String,String> queryRecord = new HashMap<>();
		Map<String,String> authorityRecord = new HashMap<>();
		queryRecord.put("id", "q");
		authorityRecord.put("id", "a");

		authorityRecord.put("full_name", "Quercus alba L.");

		// Non-blank, but always matches
		queryRecord.put("full_name", "Anything");
		test(true, queryRecord, authorityRecord, fullNameBlanksMatchOnly);
		test(1.0, queryRecord, authorityRecord, fullNameBlanksMatchOnly);

		// Blank, so blanksMatch catches it and reduces the score
		queryRecord.put("full_name", "");
		test(true, queryRecord, authorityRecord, fullNameBlanksMatchOnly);
		test(0.5, queryRecord, authorityRecord, fullNameBlanksMatchOnly);

		// Non-blank, but always matches
		queryRecord.put("full_name", "Anything");
		test(true, queryRecord, authorityRecord, fullNameBlanksDifferOnly);
		test(1.0, queryRecord, authorityRecord, fullNameBlanksDifferOnly);

		// Blank, but !blanksMatch so the AlwaysMatcher gets this and maintains the score
		queryRecord.put("full_name", "");
		test(true, queryRecord, authorityRecord, fullNameBlanksDifferOnly);
		test(1.0, queryRecord, authorityRecord, fullNameBlanksDifferOnly);
	}

	private void test(boolean expectedResult, Map<String,String> queryRecord, Map<String,String> authorityRecord, List<Property> properties) {
		logger.info("Testing Q:{} and A:{}, expecting {}", queryRecord, authorityRecord, expectedResult);
		try {
			// transform query and authority properties
			for (Property prop : properties) {
				String qfName = prop.getQueryColumnName();
				String qfValue = queryRecord.get(qfName);
				String afName = prop.getAuthorityColumnName();
				String afValue = authorityRecord.get(afName);
				qfValue = (qfValue == null) ? "" : qfValue; // super-csv treats blank as null, we don't for now
				afValue = (afValue == null) ? "" : afValue;
				// transform the field-value..
				for (Transformer t : prop.getQueryTransformers()) {
					qfValue = t.transform(qfValue);
				}
				for (Transformer t : prop.getAuthorityTransformers()) {
					afValue = t.transform(afValue);
				}
				// ..and put it into the record
				queryRecord.put(qfName + Configuration.TRANSFORMED_SUFFIX, qfValue);
				authorityRecord.put(afName + Configuration.TRANSFORMED_SUFFIX, afValue);
			}

			boolean actualResult = LuceneUtils.recordsMatch(queryRecord, LuceneUtils.map2Doc(authorityRecord), properties);
			logger.info("Result was {} ({})", actualResult, (expectedResult == actualResult) ? "Passed" : "Failed");
			Assert.assertTrue(expectedResult == actualResult);
		}
		catch (MatchException | TransformationException e) {
			Assert.fail(e.getMessage());
		}
	}

	private void test(double expectedResult, Map<String,String> queryRecord, Map<String,String> authorityRecord, List<Property> properties) {
		logger.info("Testing Q:{} and A:{}, expecting {}", queryRecord, authorityRecord, expectedResult);
		try {
			double actualResult = LuceneUtils.recordsMatchScore(queryRecord, LuceneUtils.map2Doc(authorityRecord), properties);
			logger.info("Result was {} ({})", actualResult, (Math.abs(expectedResult - actualResult) < 0.00001) ? "Passed" : "Failed");
			Assert.assertTrue(Math.abs(expectedResult - actualResult) < 0.00001);
		}
		catch (MatchException | TransformationException e) {
			Assert.fail(e.getMessage());
		}
	}
}
