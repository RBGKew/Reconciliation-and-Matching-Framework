Feature: The application exposes reconciliation (matching) functionality as an Open Refine Reconciliation Service.

	Scenario: Ensure appropriate reconciliation metadata is returned
		When I query for metadata of the "generalTest" reconciliation service
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
							"default"
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
								"default"
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
								"default"
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
							"default"
						],
						"id" : "kew-46537"
					}
				]
			}
			"""
