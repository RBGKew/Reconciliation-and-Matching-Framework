# deduplicator-framework-app

## quick introduction
This project is a generic de-duplication framework using Spring and Lucene.

It runs in two modes - deduplication and match. 
- Deduplication: 
	operates on a single data file and looks for duplicate records within it.
- Match: 
	operates on two data files, and looks for matching records between the two.
	
In order to run it needs two know two things:
- the directory `data-dir` containing the config-, input- and future output- files
- optional: the path of the `config-file` (a spring bean context file), relative to `data-dir`,
  defaults to 'config.xml'

The project is built using maven, so to run on the command-line do:
$ mvn clean compile exec:java -Dexec.args="--data-dir <path/to/directory> --config-file <path/to/config>"

For full examples look at the high-level cucumber tests, the simplest one currently
is at src/test/resources/org/kew/shs/dedupl/levenshtein_dedup.feature .

For examples of concrete Matchers/Transformers/etc look at the corresponding unit-tests.


## in more detail: what it does

### configuration

The spring context is split up in three different files:
* application-context.xml: a generic context that is always the same (independent of config details)
* application-context-dedup.xml/application-context-match.xml:
  this context adds the engine to the configuration; depending on the specific
  task this will be either a LuceneDeduplicator or a LuceneMatcher
* the config-specific context: Contains the 'user-configuration', the information
  about the data-sets (file locations and format, which columns to use in which
  way, matchers, transformers, reporters). This is the context that has to import the other two.
  A sample configuration can be found at src/test/resources/org/kew/shs/dedupl/levenshtein_dedup.feature .


### architectural design
All the work is done by a `DataHandler`, by the <engine> bean in the task specific spring context.
The actual configuration is held in the <config> bean in the user provided config that wires together
all user-configurable elements. Besides the file location and file format the most important here are
the `Properties`: they wire together the name of a column with the desired `Transformer(s)` and `Matcher`.

 -----------------------------------------------------------
|                                       [logical datamodel] |
|   Configuration                                           |         
|         |                           -< Transformer        |                     
|         |                          /                      |
|         |---------< Property ------                       |
|         |           (each col)     \                      |
|         |                           - Matcher             |
|         `---------< Reporter                              |
|                                                           |
 -----------------------------------------------------------

The Reporter(s) define the output format of the match- and deduplication tasks.

## what it should do
- the transformers including the Dictionary should be moved into their own
  module in order to make them better accessible for other uses
  (e.g. from pentahoo, what the DataImprovementTeam use a lot)

## further reading
* see the old README for more background info
