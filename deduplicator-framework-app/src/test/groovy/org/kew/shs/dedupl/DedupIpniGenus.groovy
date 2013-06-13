package org.kew.shs.dedupl

import java.nio.file.Files

import org.kew.shs.dedupl.util.DeduplApp

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import cucumber.api.DataTable
import cucumber.api.PendingException

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

// define input and output files; like this we make them available as variables for the scope
// of all steps in the scenario, but we actually create and delete them in a controlled way
def tempDir, tempConfigFile, tempInputFile, tempOutputFile

Before() {
	tempDir = Files.createTempDirectory("dedup_ipni_genus")
	tempConfigFile = new File([tempDir, "config.xml"].join(File.separator)).toPath()
	tempInputFile = new File([tempDir, "input.txt"].join(File.separator)).toPath()
	tempOutputFile = new File([tempDir, "output.txt"].join(File.separator)).toPath()
}

After() {
	tempDir.toFile().deleteDir()
	new File('target/deduplicator').deleteDir() // removes the lucene index after scenario
}

Given(~'^Rachel has created an input-file to feed the deduplicator framework containing tab-separated Genus data$') {DataTable fileContent ->
	tempInputFile.toFile().withWriter { out ->
		fileContent.asList().each {out.println it.join("\t")}
	}
}

Given(~'^Alecs has set up a genus-dedup configuration file according to her specs:$') { String configXML ->
	tempConfigFile.toFile().write(configXML.asType(String))
}

When(~'^this genus config is run through the Dedupl App$') { ->
	String[] args = ["-d", tempDir.toString() + "/"]
	DeduplApp.main(args)
}

Then(~'^a file should have been created in the same folder with the following genus data:$') { DataTable expectedOutput ->
	def expectedOutputList = expectedOutput.asList()
	tempOutputFile.toFile().readLines().eachWithIndex { line, i ->
		def expectedLine = expectedOutputList[i]
		def lineList = line.split('\t')
		lineList.eachWithIndex { col, k -> 
			try { assertThat(col, is(expectedLine[k])) }
			catch (AssertionError e) {
				String newE = e.toString() + " -- error occurred in record ${i} at <${expectedOutputList[0][k]}>"
				throw new AssertionError(newE)
			} 
		}
	}
}
