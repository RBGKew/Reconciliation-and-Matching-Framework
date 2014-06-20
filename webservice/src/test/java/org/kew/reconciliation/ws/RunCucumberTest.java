package org.kew.reconciliation.ws;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/site/cukes", "json:target/site/cukes/cucumber.json"}, monochrome = false)
public class RunCucumberTest {
}
