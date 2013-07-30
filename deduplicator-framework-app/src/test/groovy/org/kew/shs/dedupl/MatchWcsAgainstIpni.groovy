package org.kew.shs.dedupl

import java.nio.file.Files

import org.kew.shs.dedupl.util.CoreApp

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import cucumber.api.DataTable
import cucumber.api.PendingException

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

// define input and output files; like this we make them available as variables for the scope
// of all steps in the scenario, but we actually create and delete them in a controlled way
def tempDir, tempConfigFile, tempSourceFile, tempOutputFile

Before() {
    tempDir = Files.createTempDirectory("match_wcs_against_ipni")
    tempConfigFile = new File([tempDir, "config.xml"].join(File.separator)).toPath()
    tempSourceFile = new File([tempDir, "source.txt"].join(File.separator)).toPath()
    tempLookupFile = new File([tempDir, "lookup.txt"].join(File.separator)).toPath()
    tempOutputFile = new File([tempDir, "output.txt"].join(File.separator)).toPath()
}

After() {
    tempDir.toFile().deleteDir()
    new File('target/deduplicator').deleteDir() // removes the lucene index after scenario
}

Given(~'^Eszter has created an source-file to feed the deduplicator framework containing tab-separated WCS data$') { DataTable fileContent ->
    tempSourceFile.toFile().withWriter { out ->
        fileContent.asList().each {out.println it.join("\t")}
    }
}

Given(~'^she has created a lookup-file to match against$') { DataTable fileContent ->
    tempLookupFile.toFile().withWriter { out ->
        fileContent.asList().each {out.println it.join("\t")}
    }
}

Given(~'^Alecs has set up a match configuration file according to her specs:$') { String configXML ->
    tempConfigFile.toFile().write(configXML.asType(String).replaceAll("REPLACE_WITH_TMPDIR", tempDir.toString()))
}

When(~'^this match config is run through the Match App$') { ->
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
