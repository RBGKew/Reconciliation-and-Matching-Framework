# deduplicator-framework

This is a multi-module maven project. It consists of:

* the deduplicator (incl. the CoreApp): a command-line tool for deduplication
  and string matching task
* match-conf: a wrapper around the app providing a UI with (persistent)
  configuration functionality
* stringmodlib: a set of string modification packages meant for easy re-use
  as well by other apps
* stringmodutils: several classes re-used by more than one submodule

## testing
mvn clean test

## local deployment of match-conf
The following starts a local server on port 8080.
[in the root directory of deduplicator-framework:] mvn clean install
cd matchconf
mvn jetty:deploy-war

## detailed information regarding the app and match-conf
Read in the submodules:
- deduplicator-framework-app/readme.md
- matchconf/readme.md
