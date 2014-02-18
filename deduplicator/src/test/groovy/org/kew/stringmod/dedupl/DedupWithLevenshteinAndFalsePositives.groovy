package org.kew.stringmod.dedupl

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

import java.nio.file.Files

import cucumber.api.DataTable

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

// define input and output files; like this we make them available as variables for the scope
// of all steps in the scenario, but we actually create and delete them in a controlled way
def tempDir, tempConfigFile, tempInputFile, tempOutputFile, tempDictFile

Before() {
    tempDir = Files.createTempDirectory("dedup_with_levenshtein")
    tempConfigFile = new File([tempDir, "config.xml"].join(File.separator)).toPath()
    tempInputFile = new File([tempDir, "input.txt"].join(File.separator)).toPath()
    tempOutputFile = new File([tempDir, "output.txt"].join(File.separator)).toPath()
    tempDictFile = new File([tempDir, "false_positives.tsv"].join(File.separator)).toPath()
}

After() {
    tempDir.toFile().deleteDir()
    new File('target/deduplicator').deleteDir() // removes the lucene index after scenario
}
Given(~'^Alecs has created an input file as simple as before$') { DataTable fileContent ->
    tempInputFile.toFile().withWriter { out ->
        fileContent.asList().each {out.println it.join("\t")}
    }
}
Given(~'^he has added a dictionary containing the following data$') { DataTable fileContent ->
    tempDictFile.toFile().withWriter { out ->
        fileContent.asList().each {out.println it.join("\t")}
    }
}

Given(~'^Alecs has set up a config file, this time with false positives in a dictionary$') { String configXML ->
    tempConfigFile.toFile().write(configXML.asType(String).replaceAll("REPLACE_WITH_TMPDIR", tempDir.toString()))
}

When(~'^this levenshtein false-positives config is run through the Dedup App$') { ->
    String[] args = ["-d", tempDir.toString() + "/"]
    CoreApp.main(args)
}

Then(~'^a file should have been created in the same folder with the following levenshtein false-positives deduplicated records$') { DataTable expectedOutput ->
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
