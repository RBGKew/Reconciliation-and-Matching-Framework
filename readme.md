# Reconciliation and Matching Framework (RMF)

A framework to allow the configurable matching of string entities using customised sets of transformations and matchers, plus a tool to produce the necessary configurations and another to expose them as OpenRefine reconciliation services.

For more information on what it does, see the [Kew Reconciliation Services website](http://data1.kew.org/reconciliation/).

## Credits

This project was started at the [Royal Botanic Gardens, Kew](http://www.kew.org/) in 2012.  Contributors in chronological order are:

* Nicky Nicolson
* Nick Black
* Alecs Geuder
* The Data Improvement Team:
	* Anna Lynch
	* Rachel Witherow
	* Malin Rivers
	* Eszter Wainwright-Deri
* Matthew Blissett (current maintainer)

## Developer information

### Project layout

This is a multi-module Maven project.  It consists of:

* the deduplicator (incl. the CoreApp): a command-line tool for deduplication
  and string matching tasks
* matchconf: a wrapper around the app providing a UI with (persistent)
  configuration functionality
* reconciliation-service: a wrapper around the app exposing premade match
  configurations as [OpenRefine](http://www.openrefine.org/) reconciliation
  services
* reconciliation-service-model: domain objects for the reconciliation service.

### Testing

`mvn clean test`

## Local deployment of matchconf
The following starts a local server on port 8080.

1. `mvn clean install`
2. `cd matchconf`
3. `mvn jetty:deploy-war`

### Detailed information

Read in the submodules:

* [rmf-core/readme.md](rmf-core/readme.md)
* [matchconf/readme.md](matchconf/readme.md)
* [reconciliation-service/readme.md](reconciliation-service/readme.md)
