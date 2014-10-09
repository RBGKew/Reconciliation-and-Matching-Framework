# Reconciliation and Matching Framework Core

## 30 second introduction

This project is a generic reconciliation and matching (or deduplication) framework using Spring and Lucene.
It has:

*	Application configuration using Spring (I didn’t have to design a
	config file format, I just used Spring to handle this for me).
*	For the data improvement team it provides a generic deduplication framework,
	testing with configurations on IPNI names, HerbCat collection events and KBD website user accounts.

It is available on GitHub at https://github.com/RBGKew/Reconciliation-and-Matching-Framework or
internally at Kew as utilities/deduplicator-framework.git

It runs in two modes — deduplication and match.

### Deduplication:

*	operates on a single data file and looks for duplicate records within it.

### Match:

*	operates on two data files, and looks for matching records between the two.

The project is built using Maven, so to run:

`mvn clean compile exec:java -Dexec.mainClass=org.kew.rmf.core.CoreApp`

## 5 minute introduction

This project is a deduplication framework application, following an
investigation of Duke deduplication tool.  It's really a lot like Duke (using
Lucene as a flexible backend), but using Boolean matches rather than 
Bayesian.  It's using Lucene in quite a naive way at the moment: just easier
to use this than dynamically create relational DB tables, and it makes no 
assumptions about the platform.

Configuration is via Spring, so you set up transformers and matchers per 
field, as JavaBeans, so it is generic for any kind of data.  Matchers have 
the notion of cost, and get evaluated in order of cost, bailing out if 
anything mandatory fails. 

### How it works:

A columnar data file is read into the application.  Each field is mapped to a 
property.  A property object defines how field values are cleaned, stored and 
matched.  Rather than do a cartesian product (comparing each row to all others 
in the data file), some fields can be considered "blockers" by setting the 
useInSelect flag.  Prior to storage in Lucene, field values are transformed 
using the transformers declared in the configuration file.  These do things 
like epithet canonicalisation, strip non alphabetic characters etc.  (This 
configuration is in src/main/resources/*-deduplication-config.xml).  Data is 
stored in a Lucene index under the "target" subdirectory.

At a high level, the match process is:

```
for record r1 in datastore do
	build query for possible matches using values from r1
	retrieve possiblematches
	for record r2 in possiblematches
		for properties in config
			retrieve field value from r1 and r2
			evaluate match using matchers defined on this property
```

Matching pairs of ids are written out to a result file.
A match report is also written to a separate file, this displays field values 
that did not match exactly, which is useful for debugging.

Transformers are implemented in the package `org.kew.rmf.transformers`,
from the [String Transformers](https://github.com/RBGKew/String-Transformers) project.
Matchers are in the package `org.kew.rmf.matchers`.  To look at these either
browse the source, see the Javadocs (on GitHub) or build javadocs using Maven:
	`mvn javadoc:javadoc`
Then navigate to the Javadoc index page at target/site/apidocs/index.html

I've not done too much with the author matching at present other than do the 
in / ex removal as required.  It's returning micro/macro as matches e.g.
39284-2/186952-1.  I guess it might need a post-processor / sanity-checker to 
catch these.

To prove that it really is generic, I've run it on a variety of data — IPNI
name citations, HerbCat collection events and KBD website user accounts, all 
with just configuration changes.  I have split the Spring configuration so 
that relating to the data file (the definition of properties, transformers 
and matchers) is separate to the rest of the Spring config (which really just 
defines the Lucene datastore).  The domain-specific config file is defined in 
the main Spring config file using an import statement, line #12).

Further work:
*	Implement some kind of configurable post-processor to false positives
	(it should be explicit about what it does, eg accept micro/macro pair
	in configuration).
*	The Lucene processing could be more efficient — could try using Lucene 4
	which has greatly improved the performance, particularly on fuzzy
	matching.
*	Extend for different datasets by adding new transformers and / or
	matchers.

## Using it to match WCS against IPNI

1. Assemble data

1.1. Get Cyperaceae data for species only from IPNI:

```
select concat(ipniflat.citation_superset.Id
	,'\t', Family
	,'\t', Full_name_without_family_and_authors
	,'\t', Genus
	,'\t', Species
	,'\t', Basionym_author
	,'\t', Publishing_author
	,'\t', Collation
	,' ', case Publication_year when '0' then '' else Publication_year end
	,'\t', Publication)
from ipniflat.citation_superset
	where Family IN ('Cyperaceae')
	AND rank = 'spec.'
	AND Hybrid_genus = 'N'
	AND Hybrid = 'N'
```

1.2. Get Cyperaceae data for species only from WCS:

```
SELECT DISTINCT convert(varchar,Plant_Name.Plant_name_id)
	||CHAR(9)|| Plant_Name.Family
	||CHAR(9)|| Plant_Name.Full_epithet
	||CHAR(9)|| Plant_Name.Genus
	||CHAR(9)|| Plant_Name.Species
	||CHAR(9)|| bas_auths.Author
	||CHAR(9)|| pri_auths.Author
	||CHAR(9)|| Plant_Name.Volume_and_page
	||' '|| str_REPLACE(str_REPLACE(str_REPLACE(Plant_Name.First_published, CHAR(10), ''), CHAR(13), ''), CHAR(9), '')
	||CHAR(9)|| str_REPLACE(str_REPLACE(str_REPLACE(Place_of_Publication.Place_of_publication, CHAR(10), ''), CHAR(13), ''), CHAR(9), '')
FROM Plant_Name  
	INNER JOIN Taxon_Status ON Plant_Name.Taxon_status_id = Taxon_Status.Taxon_status_id
	INNER JOIN Plant_Author AS pri_auth_link ON pri_auth_link.Plant_name_id = Plant_Name.Plant_name_id
	INNER JOIN Plant_Author AS bas_auth_link ON bas_auth_link.Plant_name_id = Plant_Name.Plant_name_id
	INNER JOIN Authors AS pri_auths ON pri_auth_link.Author_id = pri_auths.Author_id
	LEFT JOIN Authors AS bas_auths ON bas_auth_link.Author_id = bas_auths.Author_id
	INNER JOIN Place_of_Publication ON Plant_Name.Place_of_publication_id = Place_of_Publication.Place_of_publication_id
WHERE Family IN ('Cyperaceae')
	AND Plant_Name.Infraspecific_rank IS NULL
	AND Plant_Name.Infraspecific_epithet IS NULL
	AND Plant_Name.Species IS NOT NULL
	AND Plant_Name.Species_hybrid_marker IS NULL
	AND Plant_Name.Genus_hybrid_marker IS NULL
	AND Plant_Name.Plant_name_id > 0
	AND pri_auth_link.Author_type_id = 'PRM'
	AND bas_auth_link.Author_type_id = 'PAR'
```

2. Configure the dedupl-framework app to match these

- Family — must match exactly
- Full epithet — for info only — always match
- Genus — must match exactly
- Species — Levenshtein of 1, initial character the same
- Basionym author — blanks match
- Primary author
- Volume, page and year — must share common tokens
- Publication title — anything goes

Store the data-set with the most data, iterate over the data-set with the least.
