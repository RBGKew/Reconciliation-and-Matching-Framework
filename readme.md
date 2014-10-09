# Reconciliation and Matching Framework (RMF)

This is a multi-module Maven project.  It consists of:

* the deduplicator (incl. the CoreApp): a command-line tool for deduplication
  and string matching tasks
* matchconf: a wrapper around the app providing a UI with (persistent)
  configuration functionality
* reconciliation-service: a wrapper around the app exposing premade match
  configurations as [OpenRefine](http://www.openrefine.org/) reconciliation
  services
* reconciliation-service-model: domain objects for the reconciliation service.

## Testing

`mvn clean test`

## Local deployment of matchconf
The following starts a local server on port 8080.

1. `mvn clean install`
2. `cd matchconf`
3. `mvn jetty:deploy-war`

## Detailed information
Read in the submodules:

* [rmf-core/readme.md](rmf-core/readme.md)
* [matchconf/readme.md](matchconf/readme.md)
* [reconciliation-service/readme.md](reconciliation-service/readme.md)
