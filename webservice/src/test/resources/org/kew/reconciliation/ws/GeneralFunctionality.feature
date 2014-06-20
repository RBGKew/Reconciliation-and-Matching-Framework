Feature: Functionality not particular to a specific match configuration is tested here.  For example, the JSON response, Reconciliation service API.

	Scenario: Ensure appropriate reconciliation metadata is returned
		When I query for reconciliation service metadata
		Then I receive the following metadata response:
			"""
			{
				"name" : "General Test Match Reconciliation Service",
				"identifierSpace" : "http://kew.test/identifierSpace",
				"schemaSpace" : "http://kew.test/schemaSpace",
				"view" : {
					"url" : "http://kew.test/{{id}}"
				},
				"preview" : {
					"url" : "http://kew.test/preview/{{id}}",
					"width" : 400,
					"height" : 400
				},
				"suggest" : {
					"type" : {
						"service_url" : "http://kew.test",
						"service_path" : "/suggestType",
						"flyout_service_url" : "http://www.kew.test"
					},
					"property" : {
						"service_url" : "http://kew.test",
						"service_path" : "/suggestProperty",
						"flyout_service_url" : "http://www.kew.test"
					},
					"entity" : {
						"service_url" : "http://kew.test",
						"service_path" : "/suggest",
						"flyout_service_path" : "/flyout"
					}
				},
				"defaultTypes" : null
			}
			"""

	Scenario: Ensure a standard (non-reconciliation API) query returns a suitable JSON response
		When I make a match query for "genus=Congea&species=chinensis&authors=Moldenke"
		Then I receive the following match response:
			"""
			[
				{
					"taxonomicStatus_transf" : "Accepted",
					"taxonomicStatus" : "Accepted",
					"id" : "kew-46537",
					"source_authors_transf" : "mol",
					"authors" : "Moldenke",
					"genus_transf" : "Congea",
					"species_transf" : "chinensis",
					"source_acceptedNameID_transf" : "",
					"authors_transf" : "mol",
					"genus" : "Congea",
					"infraspecies" : "",
					"species" : "chinensis",
					"acceptedNameID" : "kew-46537",
					"source_species_transf" : "chinensis",
					"source_genus_transf" : "Congea",
					"infraspecies_transf" : "",
					"source_infraspecies_transf" : "",
					"acceptedNameID_transf" : "kew-46537",
					"source_taxonomicStatus_transf" : ""
				}
			]
			"""
