# Copyright © 2014 Royal Botanic Gardens, Kew.  See LICENSE.md for details.

Feature: Basic functionality of the application: responding to a match query.

	Scenario: Ensure a standard (non-reconciliation API) query returns a suitable JSON response
		When I make a match query for "genus=Congea&species=chinensis&authors=Moldenke"
		Then I receive the following match response:
			"""
			[
				{
					"taxonomicStatus_transf" : "Accepted",
					"taxonomicStatus" : "Accepted",
					"id" : "kew-46537",
					"authors" : "Moldenke",
					"genus_transf" : "Congea",
					"species_transf" : "chinensis",
					"authors_transf" : "mol",
					"genus" : "Congea",
					"infraspecies" : "",
					"species" : "chinensis",
					"acceptedNameID" : "kew-46537",
					"infraspecies_transf" : "",
					"acceptedNameID_transf" : "kew-46537",
					"_score" : "1.0000",
				}
			]
			"""
		And cross-site access is permitted

	Scenario: Reconcile records by file upload
		When I make a bulk match query with a file containing these rows:
			"""
			id,genus,species,infraspecies,authors
			1,Congea,forbesii,,King & Gamble
			2,Congeus,forbesi,,King & Gamble
			3,Congoops,forbesii,,King & Gamble
			4,Congeus,villosa,,
			"""
		Then I receive the following result file:
			"""
			queryId,matchId
			1,kew-46541
			2,kew-46541
			3,
			4,tro-50269022|kew-46562
			"""
		And cross-site access is permitted

#	Scenario: Ensure many simultaneous / rapid queries give the correct responses
#		It's not clear how to test this.
