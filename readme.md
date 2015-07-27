# Reconciliation and Matching Framework (RMF)

A framework to allow the configurable matching of string entities using customised sets of transformations and matchers, plus a tool to produce the necessary configurations and another to expose them as OpenRefine reconciliation services.

For more information on what it does, see the [Kew Reconciliation Services website](http://data1.kew.org/reconciliation/).

## Credits

This project is developed and maintained by the [Biodiversity Informatics team](http://www.kew.org/science-conservation/people-and-data/science-directory/teams/biodiversity-informatics-and-spatial) at the [Royal Botanic Gardens, Kew](http://www.kew.org/). Development started in 2012.  Contributors in chronological order are:

* Nicky Nicolson (2012- )
* Alecs Geuder (2013-14)
* Matthew Blissett (2014-)

Development and maintenance have been supported by several projects:

* Science and Horticulture Systems project, funded by the UK government (Department for Environment, Food and Rural Affairs). Supported initial development, and a data improvement team (Anna Lynch, Rachel Witherow, Malin Rivers, Eszter Wainwright-Deri). 
* Medicinal Plant Names Services project, funded by the Wellcome Trust (technical contributions from Nick Black)
* Plants of the World Online (on-going)

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

Some tests in the `reconciliation-service` package connect to databases to check reconciliation results.  Passwords need to be supplied on the command line:

`mvn clean install -Dipni.database.password=XXX -Dipniflat.database.password=XXX -Dtpl.database.password=XXX`

### Detailed information

Read in the submodules:

* [rmf-core/readme.md](rmf-core/readme.md)
* [matchconf/readme.md](matchconf/readme.md)
* [reconciliation-service/readme.md](reconciliation-service/readme.md)
