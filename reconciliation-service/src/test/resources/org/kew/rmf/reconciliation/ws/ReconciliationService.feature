# Copyright © 2014 Royal Botanic Gardens, Kew.  See LICENSE.md for details.

Feature: The application exposes reconciliation (matching) functionality as an Open Refine Reconciliation Service.

	Scenario: Ensure appropriate reconciliation metadata is returned
		When I query for metadata of the "generalTest" reconciliation service
		Then I receive the following metadata response:
			"""
			{
				"name" : "General Test Match Reconciliation Service (unknown)",
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
						"service_url" : "http://localhost",
						"service_path" : "/reconcile/generalTest/suggestType",
						"flyout_service_path" : "/reconcile/generalTest/flyoutType/${id}",
						"flyout_service_url" : "http://localhost"
					},
					"property" : {
						"service_url" : "http://localhost",
						"service_path" : "/reconcile/generalTest/suggestProperty",
						"flyout_service_path" : "/reconcile/generalTest/flyoutProperty/${id}",
						"flyout_service_url" : "http://localhost"
					},
					"entity" : {
						"service_url" : "http://localhost",
						"service_path" : "/reconcile/generalTest",
						"flyout_service_url" : "http://localhost",
						"flyout_service_path" : "/reconcile/generalTest/flyout/${id}"
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
						"name" : "Congea chinensis Moldenke (Accepted)",
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
							"name" : "Congea chinensis Moldenke (Accepted)",
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
							"name" : "Congea munirii Moldenke (Accepted)",
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

	Scenario: When queried without the key property (first property, here genus) the query
		is taken to be the key property.
		When I make the reconciliation query:
			"""
			{
				"query" : "Congea",
				"properties" : [
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
						"name" : "Congea chinensis Moldenke (Accepted)",
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
		When I make the reconciliation query:
			"""
			{
				"query" : "X",
				"properties" : [
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
				"result" : []
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
						"name" : "Congea chinensis Moldenke (Accepted)",
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
						"name" : "Congea villosa Voigt (Synonym)",
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
						"name" : "Congea villosa Wight (Synonym)",
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
						"name" : "Congea villosa Voigt (Synonym)",
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

	Scenario: The Suggest Flyout returns HTML wrapped in a JSON object when queried with an id.
		In Open Refine, the HTML is shown when the user "Search for match" function and highlights a result.
		When I make the reconciliation suggest flyout request with id "tro-50269022"
		Then I receive the following flyout response:
			"""
			{
				"html" : "<html><body>tro-50269022</body></html>"
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
