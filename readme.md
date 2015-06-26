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

       ╔════════════════╗ ╔═════════════════════════╗
       ║   Web browser  ║ ║        OpenRefine       ║
       ╚═══╤═════════╤══╝ ╚═════╤╤╤══════════════╤══╝
           │         │          │││              │
           │         │          │││1. Reconcile  │ 2. Extend
           │         │          │││              │
           │         │          │││              │
    ┏━━━━━━┷━━━━━━━┳━┷━━━━━━━━━━┷┷┷━━━━━━━━┓   ╔═╧══════════════╗
    ┃  MatchConf   ┃Reconciliation Service ┃   ║Kew MQL services║
    ┃(Expert users)┃ (Match names to IPNI) ┃   ║    e.g TPL     ║
    ┣━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━┫   ╚════════════════╝
    ┃Reconciliation and Matching Framework ┃
    ┃                Core                  ┃
    ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
    ┃         String transformers          ┃
    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

#### Maven submodules:

* rmf-core: previously been referred to as the "Deduplicator" or "Name Matcher".
  It's a command-line tool for deduplication and string matching tasks.
* matchconf: provides an expert interface for producing custom matching configurations.
  It provides a UI with persistent configuration functionality.
  At present there are no active users.
* reconciliation-service is a wrapper around the core, exposing pre-made configurations
  as [OpenRefine](http://www.openrefine.org/) reconciliation services.
  It's best used through OpenRefine, but also presents a web interface
  for individual queries and bulk CSV upload.
* reconciliation-service-model: domain objects for the reconciliation service.

#### External pieces shown above:

* The String Transformers library
* MQL services

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
