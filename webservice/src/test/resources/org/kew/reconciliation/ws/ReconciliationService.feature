Feature: The application exposes reconciliation (matching) functionality as an Open Refine Reconciliation Service.

	Scenario: Ensure appropriate reconciliation metadata is returned
		When I query for metadata of the "generalTest" reconciliation service
		Then I receive the following metadata response:
			"""
			{
				"name" : "General Test Match Reconciliation Service",
				"identifierSpace" : "http://www.theplantlist.org/tpl1.1/record/",
				"schemaSpace" : "http://rdf.freebase.com/ns/type.object.id",
				"view" : {
					"url" : "http://www.theplantlist.org/tpl1.1/record/{{id}}?reconcileView"
				},
				"preview" : {
					"url" : "http://www.theplantlist.org/tpl1.1/record/{{id}}?reconcilePreview",
					"width" : 400,
					"height" : 400
				},
				"suggest" : {
					"type" : {
						"service_url" : "http://localhost:8080",
						"service_path" : "/reconcile/generalTest/suggestType",
						"flyout_service_url" : "http://www.kew.test"
					},
					"property" : {
						"service_url" : "http://localhost:8080",
						"service_path" : "/reconcile/generalTest/suggestProperty",
						"flyout_service_url" : "http://www.kew.test"
					},
					"entity" : {
						"service_url" : "http://localhost:8080",
						"service_path" : "/reconcile/generalTest",
						"flyout_service_url" : "http://www.theplantlist.org",
						"flyout_service_path" : "/tpl1.1/record/${id}?reconcileSuggest"
					}
				},
				"defaultTypes" : [
					{
						"id" : "/biology/organism_classification/scientific_name",
						"name" : "Scientific name"
					}
				]
			}
			"""

	Scenario: Ensure appropriate reconciliation metadata is returned (error check).
		When I query for metadata of the "nonExistent" reconciliation service
		Then I receive an HTTP 404 result.

	Scenario: Ensure a single reconciliation query gives the correct response
		When I make the reconciliation query:
			"""
			{
				"query" : "Ignored Congea chinensis",
				"properties" : [
					{
						"pid" : "genus",
						"v" : "Congea"
					},
					{
						"pid" : "species",
						"v" : "chinensis"
					},
					{
						"pid" : "authors",
						"v" : "Moldenke"
					}
				]
			}
			"""
		Then I receive the following reconciliation response:
			"""
			{
				"result" : [
					{
						"match" : true,
						"name" : "Congea chinensis Moldenke",
						"score" : 100,
						"type" : [
							{
								"id" : "/biology/organism_classification/scientific_name",
								"name" : "Scientific name"
							}
						],
						"id" : "kew-46537"
					}
				]
			}
			"""

	Scenario: Ensure a multiple reconciliation query gives the correct response
		When I make the reconciliation queries:
			"""
			{
				"q1" : {
					"query" : "Ignored Congea chinensis",
					"properties" : [
						{
							"pid" : "genus",
							"v" : "Congea"
						},
						{
							"pid" : "species",
							"v" : "chinensis"
						},
						{
							"pid" : "authors",
							"v" : "Moldenke"
						}
					]
				},
				"q2" : {
					"query" : "Ignored Congea munirii",
					"properties" : [
						{
							"pid" : "genus",
							"v" : "Congea"
						},
						{
							"pid" : "species",
							"v" : "munirii"
						},
						{
							"pid" : "authors",
							"v" : "Moldenke"
						}
					]
				}
			}
			"""
		Then I receive the following reconciliation multiple response:
			"""
			{
				"q1" : {
					"result" : [
						{
							"match" : true,
							"name" : "Congea chinensis Moldenke",
							"score" : 100,
							"type" : [
								{
									"id" : "/biology/organism_classification/scientific_name",
									"name" : "Scientific name"
								}
							],
							"id" : "kew-46537"
						}
					]
				},
				"q2" : {
					"result" : [
						{
							"match" : true,
							"name" : "Congea munirii Moldenke",
							"score" : 100,
							"type" : [
								{
									"id" : "/biology/organism_classification/scientific_name",
									"name" : "Scientific name"
								}
							],
							"id" : "kew-46548"
						}
					]
				}
			}
			"""

	Scenario: Provide a "best effort" match when queried with only a single string, without
		genus, species etc parameters.
		When I make the reconciliation query:
			"""
			{
				"query" : "Congea chinensis Moldenke"
			}
			"""
		Then I receive the following reconciliation response:
			"""
			{
				"result" : [
					{
						"match" : true,
						"name" : "Congea chinensis Moldenke",
						"score" : 100,
						"type" : [
							{
								"id" : "/biology/organism_classification/scientific_name",
								"name" : "Scientific name"
							}
						],
						"id" : "kew-46537"
					}
				]
			}
			"""

	Scenario: Match score should be 100/(number of results), and match result false if there's more than one.
		When I make the reconciliation query:
			"""
			{
				"query" : "Congea villosa"
			}
			"""
		Then I receive the following reconciliation response:
			"""
			{
				"result" : [
					{
						"match" : false,
						"name" : "Congea villosa Voigt",
						"score" : 50.0,
						"type" : [
							{
								"id" : "/biology/organism_classification/scientific_name",
								"name" : "Scientific name"
							}
						],
						"id" : "tro-50269022"
					},
					{
						"match" : false,
						"name" : "Congea villosa Wight",
						"score" : 50.0,
						"type" : [
							{
								"id" : "/biology/organism_classification/scientific_name",
								"name" : "Scientific name"
							}
						],
						"id" : "kew-46562"
					}
				]
			}
			"""

	Scenario: The Suggest call returns possible reconciliation matches when queried with a prefix.
		In Open Refine, the result is shown when the user uses the "Search for match" function.
		When I make the reconciliation suggest query with prefix "Congea villosa Voigt"
		Then I receive the following match response:
			"""
			{
				"result" : [
					{
						"match" : true,
						"name" : "Congea villosa Voigt",
						"score" : 100.0,
						"type" : [
							{
								"id" : "/biology/organism_classification/scientific_name",
								"name" : "Scientific name"
							}
						],
						"id" : "tro-50269022"
					}
				]
			}
			"""

	Scenario: The Suggest Type call returns possible types when queried with a prefix.
		In Open Refine, the result is shown when the user begins typing in the "Reconcile against type" box.
		When I make the reconciliation suggest type query with prefix "x"
		Then I receive the following match response:
			"""
			{
				"result": [
					{
						"id" : "/biology/organism_classification/scientific_name",
						"name" : "Scientific name"
					}
				]
			}
			"""

	Scenario: The Suggest Property call returns possible properties when queried with a prefix.
		In Open Refine, the result is shown when the user begins typing in the "As Property" boxes.
		When I make the reconciliation suggest property query with prefix "s"
		Then I receive the following match response:
			"""
			{
				"result": [
					{
						"id" : "species",
						"name" : "species"
					}
				]
			}
			"""
