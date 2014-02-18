package org.kew.stringmod.dedupl

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

import java.nio.file.Files

import cucumber.api.DataTable

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
    tempOutputMultilineFile = new File([tempDir, "output_multiline.txt"].join(File.separator)).toPath()
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
    tempConfigFile.toFile().write(configXML.asType(String).replaceAll("REPLACE_WITH_TMPDIR", tempDir.toString()))
}

When(~'^this genus config is run through the Dedupl App$') { ->
    String[] args = ["-d", tempDir.toString() + "/"]
    CoreApp.main(args)
}

Then(~'^a file should have been created in the same folder with the following genus data:$') { DataTable expectedOutput ->
    def expectedOutputList = expectedOutput.asList()
    def output = tempOutputFile.toFile().readLines()
    expectedOutputList.eachWithIndex { expectedLine, i ->
        def lineList = output[i].split('\t')
        expectedLine.eachWithIndex { col, k ->
            try {
                assertThat(lineList[k], is(col))
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException("Columns missing!: ${expectedOutputList[k..-1]}")
            } catch (AssertionError e) {
                String newE = e.toString() + " -- error occurred in record ${i} at <${expectedOutputList[0][k]}>"
                throw new AssertionError(newE)
            }
        }
	}
}
	
Then(~'^a file for the multiline output should have been created in the same folder with the following genus data:$') { DataTable expectedOutput ->
    def expectedOutputList = expectedOutput.asList()
    def output = tempOutputMultilineFile.toFile().readLines()
    expectedOutputList.eachWithIndex { expectedLine, i ->
        def lineList = output[i].split('\t')
        expectedLine.eachWithIndex { col, k ->
            try {
                assertThat(lineList[k], is(col))
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException("Columns missing!: ${expectedOutputList[k..-1]}")
            } catch (AssertionError e) {
                String newE = e.toString() + " -- error occurred in record ${i} at <${expectedOutputList[0][k]}>"
                throw new AssertionError(newE)
            }
        }
	}
}
