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
package org.kew.rmf.reconciliation.ws;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:/META-INF/spring/cucumber.xml")
public class WebInterfaceStepdefs {
	//private static Logger log = LoggerFactory.getLogger(WebInterfaceStepdefs.class);

	//private static final String TEXT_HTML_UTF8 = "text/html;charset=UTF-8";

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	//private MvcResult result;
	//private Document html;
	//private Element propertyTransformers;
	//private Element propertyMatcher;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.build();

		// TODO: Temporary fix to allow test configuration to have loaded.
		// Once the ReconciliatioService allows querying for this, this should be replaced.
		try {
			Thread.sleep(2L * 1000L);
		}
		catch (InterruptedException e) {}
	}

	@Given("^I am on the about page for \"(.*?)\"$")
	public void i_am_on_the_about_page_for(String configName) throws Throwable {
		// Call
		//result =
		mockMvc.perform(get("/about/"+configName)) //.accept(MediaType.TEXT_HTML))
				.andDo(print())
				.andExpect(status().isOk())
				//.andExpect(content().contentType(TEXT_HTML_UTF8))
				.andExpect(view().name("about-matcher"))
				.andExpect(MockMvcResultMatchers.model().attribute("configName", "generalTest"))
				.andReturn();

		// Because we are using JSPs it's not possible to check their execution — we need a servlet container to execute them.
		//Document html = Jsoup.parse(result.getResponse().getContentAsString());
	}

	@When("^I look at the detail for the property \"(.*?)\"$")
	public void i_look_at_the_detail_for_the_property(String property) throws Throwable {
		throw new PendingException("Can't check contents of JSPs without a servlet container to execute them.");
		//log.info("Want to select {}", "#"+property+"_transformers");
		//propertyTransformers = html.select("#"+property+"_transformers").first();
		//propertyMatcher = html.select("#"+property+"_matcher").first();
	}

	@Then("^the transformers are$")
	public void the_transformers_are(List<Map<String,String>> values) throws Throwable {
		//for (Element transformer : propertyTransformers.children()) {
		//	transformer.select("span").first().text().equals("Epithet Transformer");
		//	transformer.select("span").first().attr("title").equals("org.kew.stuff");
		//	transformer.select("span").get(1).text().equals("replacement: \"\"");
		//}
		throw new PendingException();
	}

	@Then("^the matcher is$")
	public void the_matcher_is(List<Map<String,String>> values) throws Throwable {
		throw new PendingException();
	}

	@When("^I type in the atomized name details:$")
	public void i_type_in_the_atomized_name_details(Map<String,String> details) throws Throwable {
		throw new PendingException();
	}

	@When("^run the query$")
	public void run_the_query() throws Throwable {
		throw new PendingException();
	}

	@Then("^the results are$")
	public void the_results_are(Map<String,String> results) throws Throwable {
		throw new PendingException();
	}

	@When("^I type in the unatomized name \"(.*?)\"$")
	public void i_type_in_the_unatomized_name(String arg1) throws Throwable {
		throw new PendingException();
	}
}
