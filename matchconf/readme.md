# MatchConf

## Quick introduction

MatchConf is a wrapper around the deduplicator-framework that does two things:

*	it provides a basic web-based user interface to configure and run the
	deduplicator
*	it stores the match- and deduplication configurations in a database

## Installation & deployment

[at root level of the main maven module:]

```
$ mvn clean install deduplicator-framework
$ cd matchconf
$ mvn jetty:deploy-war
```

The last command starts a jetty server instance at the default port, 8080.
You can open a browser and start playing around at
[http://localhost:8080/MatchConf](http://localhost:8080/MatchConf).

## What it does

MatchConf doesnt 'do' anything other than storing configurations of match- and
deduplication procedures. These configuration models are not at all connected
to the RMF Core, the communication exclusively happens via a configuration
being written out as XML. The CoreApp uses this XML as a spring bean context,
the only 'direct' connection at code-level is that MatchConf can run the
CoreApp providing the location of the previously written XML file.

1. Matching and deduplication

The deduplication of a data-set is very similar to matching two data-sets to
each other, only that a deduplication uses the same data-set twice, as 'query'
and as 'authority'. Hence on code-level deduplication and match configs look very
similar. The UI however does obviously separate them.

2. ORM functionality

MatchConf models the functional ingredients of a match- or deduplication
configuration as DAOs.

(disclaimer: It has been developed using spring roo without knowing
spring roo, so its very likely that the realisation is badly done.)

Logically it is obviously similar to the classes used in the
deduplicator-framework-app (see related readme):

 -----------------------------------------------------------
|                                       [logical datamodel] |
|   Configuration                                           |
|         |                                                 |
|         |---------< Transformer -------< WiredTransformer |
|         |                                   \/    \/      |
|         |---------< Matcher                 /     ·       |
|         |                                  /     ·        |
|         |---------< Wire ------------------······         |
|         |                                                 |
|         `---------< Reporter                              |
|                                                           |
 -----------------------------------------------------------

The basic principle is that a Configuration functions as a sandbox, all
instances of the Transformer, Matcher, Wire (a 'Property' in the deduplicator)
and Reporter classes are unique for each Configuration instance. This makes it
possible to easily clone configurations and then modify some bits and pieces
without automatically changing other configurations.

The necessary addition here is the WiredTransformer. This is due to the fact
that a configuration can have 0:n transformers assigned without being wired,
also, a wire can have 0:n query-transformers and 0:n authority-transformers.

The sandbox paradigm is not the case for Dictionaries, as they are meant to be
re-usable by all configurations.

2. Communication deduplicator <-> MatchConf

## What it should do

### UI testing
There is currently no front-end testing at all. That is very bad.

### Extendibility
*	org.kew.rmf.matchconf.web.LibraryScanner defines the packages available
	within the library;
**		a configuration could have a field where you can add new packages (that
		have to be on the classpath) defining them comma-separated in a string
		("org.a.b.c.*, com.bla.blub.*")

## known bugs
* the ORM is probably very broken due to my lack of knowledge of JPA/ROO; the
  most annoying bug is that the way the WiredTransformer is implemented, they
  sometimes get orphaned in a deletion process and cause constraint violation
  errors. All attempts to fix this have not been satisfying so far. I
  originally built this webapp in Grails and did not encounter similar errors.

TODO: dealing with logs and space!
