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
					"acceptedNameID_transf" : "kew-46537"
				}
			]
			"""

#	Scenario: Ensure many simultaneous / rapid queries give the correct responses
#		It's not clear how to test this.
