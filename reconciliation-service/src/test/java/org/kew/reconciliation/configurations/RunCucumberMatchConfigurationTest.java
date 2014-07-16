package org.kew.reconciliation.configurations;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/site/cukes/configurations", "json:target/site/cukes/configurations/cucumber.json"}, monochrome = false)
public class RunCucumberMatchConfigurationTest {
	// TODO: These tests, and the configurations they test, do not depend on the webservice and should be refactored into a separate module.
}
