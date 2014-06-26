package org.kew.reconciliation.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:cucumber.xml")
public class MatchConfigurationTestingStepdefs {
	private final static Logger logger = LoggerFactory.getLogger(MatchConfigurationTestingStepdefs.class);

	@Autowired
	private GenericWebApplicationContext wac;

	LuceneMatcher currentMatcher;
	List<Map<String,String>> queryResults;

	@Given("^I have loaded the \"(.*?)\" configuration$")
	public void i_have_loaded_the_configuration(String fileName) throws Throwable {
		currentMatcher = getConfiguration(fileName);
		Assert.assertNotNull("Failed to load matcher", currentMatcher);
	}


	@When("^I query for$")
	public void i_query_for(List<Map<String,String>> queries) throws Throwable {
		Assert.assertNotNull("No matcher selected", currentMatcher);

		// Go through the list of queries one at a time, execute the query, store the result in a new key "result".

		queryResults = new ArrayList<>();

		for (Map<String,String> testQuery : queries) {
			Map<String,String> result = doSingleTestQuery(testQuery);
			logger.debug("Query result: {}", result);
			queryResults.add(result);
		}
	}

	@Then("^the results are$")
	public void the_results_are(List<Map<String,String>> expectedResults) throws Throwable {
		for (Map<String,String> expectedResult : expectedResults) {
			checkResult(expectedResult);
		}
	}

	private void checkResult(Map<String,String> expectedResult) {
		String targetId = expectedResult.get("queryId");

		Map<String,String> actualResult = null;
		for (Map<String,String> result : queryResults) {
			if (targetId.equals(result.get("queryId"))) {
				actualResult = result;
				break;
			}
		}

		Assert.assertNotNull("No result found for query "+targetId, actualResult);

		Assert.assertThat("Match results not correct", expectedResult.get("results"), Matchers.equalTo(actualResult.get("results")));
	}

	private Map<String,String> doSingleTestQuery(Map<String,String> origQuery) throws Exception {
		// Copy the map, as Cucumber supplies an UnmodifiableMap
		Map<String,String> query = new HashMap<>();
		query.putAll(origQuery);

		List<Map<String,String>> matches = currentMatcher.getMatches(query, 50);

		logger.debug("Found some matches: {}", matches.size());
		if (matches.size() < 4) {
			logger.debug("Matches for {} are {}", query, matches);
		}

		ArrayList<String> matchedIds = new ArrayList<>();
		for (Map<String,String> match : matches) {
			matchedIds.add(match.get("id"));
		}
		Collections.sort(matchedIds);

		query.put("results", StringUtils.join(matchedIds, " "));

		return query;
	}

	private static final Map<String, LuceneMatcher> matchers = new HashMap<String, LuceneMatcher>();

	private LuceneMatcher getConfiguration(String configFile) throws Throwable {
		logger.debug("Considering initialising match controller with configuration {}", configFile);

		// Load up the matchers from the specified files
		if (!matchers.containsKey(configFile)){
			logger.debug("Loading configuration {}", configFile);
			ConfigurableApplicationContext context = new GenericXmlApplicationContext(configFile);
			LuceneMatcher matcher = context.getBean("engine", LuceneMatcher.class);
			matcher.loadData();
			logger.debug("Loaded data for configuration {}", configFile);
			matchers.put(configFile, matcher);
			logger.debug("Stored matcher with name {} from file {}", matcher.getConfig().getName(), configFile);
		}

		return matchers.get(configFile);
	}
}
