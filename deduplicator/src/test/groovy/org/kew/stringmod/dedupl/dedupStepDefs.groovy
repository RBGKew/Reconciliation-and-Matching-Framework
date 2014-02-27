package org.kew.stringmod.dedupl

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

import java.nio.file.Files

import cucumber.api.DataTable

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

// define input and output files; like this we make them available as variables for the scope
// of all steps in the scenario, but we actually create and delete them in a controlled way
def tempDir, tempConfigFile, tempSourceFile, tempLookupFile, tempOutputFile, dictFile

Before() {
    tempDir = Files.createTempDirectory("dedup-dir")
    tempConfigFile = new File([tempDir, "config.xml"].join(File.separator)).toPath()
	tempSourceFile = new File([tempDir, "source.txt"].join(File.separator)).toPath()
	// tempLookupFile shall be used additionally for matching tasks..
	tempLookupFile = new File([tempDir, "lookup.txt"].join(File.separator)).toPath()
    tempOutputFile = new File([tempDir, "output.txt"].join(File.separator)).toPath()
    tempOutputMultilineFile = new File([tempDir, "output_multiline.txt"].join(File.separator)).toPath()
    dictFile = new File([tempDir, "funkyDict.txt"].join(File.separator)).toPath()
}

After() {
    tempDir.toFile().deleteDir()
    new File('target/deduplicator').deleteDir() // removes the lucene index after scenario
}

Given(~'^(?:.*) has created an (?:.*)file(?:.*)$') { DataTable fileContent ->
    tempSourceFile.toFile().withWriter { out ->
        fileContent.asList().each {out.println it.join("\t")}
    }
}
Given(~'^(?:he|she) has created a lookup-file to match against$') { DataTable fileContent ->
    tempLookupFile.toFile().withWriter { out ->
        fileContent.asList().each {out.println it.join("\t")}
    }
}

Given(~'^(?:he|she) has access to a tab-separated dictionary$') { DataTable dictContent ->
	dictFile.toFile().withWriter { out ->
		dictContent.asList().each {out.println it.join("\t")}
	}
}

Given(~'^Alecs has set up a (?:.*)configuration file(?:.*)$') { String configXML ->
	String escapedTempPath = tempDir.toString().replace("\\", "\\\\")
    tempConfigFile.toFile().write(configXML.asType(String).replaceAll("REPLACE_WITH_TMPDIR", escapedTempPath))
}

When(~'^this config is run through the deduplicator$') { ->
    String[] args = ["-d", tempDir.toString() + "/"]
    CoreApp.main(args)
}

Then(~'^a file should have been created in the same folder with the following data:$') { DataTable expectedOutput ->
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

Then(~'^a file for the multiline output should have been created in the same folder with the following data:$') { DataTable expectedOutput ->
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