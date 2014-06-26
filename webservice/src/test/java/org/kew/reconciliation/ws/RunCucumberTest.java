package org.kew.reconciliation.ws;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/site/cukes/ws", "json:target/site/cukes/ws/cucumber.json"}, monochrome = false)
public class RunCucumberTest {
}
