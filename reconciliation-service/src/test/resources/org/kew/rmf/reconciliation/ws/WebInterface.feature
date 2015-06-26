# Copyright © 2014 Royal Botanic Gardens, Kew.  See LICENSE.md for details.

Feature: The web interface describes the available services.

	Scenario: The user wants to know the detail of the configuration of the transformers in the configuration
	Given I am on the about page for "generalTest"
	When I look at the detail for the property "genus"
	Then the transformers are
		| name                                        | title                                                            | configuration                                                      |
		| Fake Hybrid Sign Cleaner                    | org.kew.rmf.transformers.botany.FakeHybridSignCleaner            | remove multiple whitespaces: true, replacement: " ", trim it: true |
		| Strip Non Alphabetic Characters Transformer | org.kew.rmf.transformers.StripNonAlphabeticCharactersTransformer | replacement: ""                                                    |
		| Epithet Transformer                         | org.kew.rmf.transformers.botany.EpithetTransformer               |                                                                    |
	And the matcher is
		| name          | title                             | configuration |
		| Exact Matcher | org.kew.rmf.matchers.ExactMatcher |               |

	Scenario: The user submits a single, atomized query using the web interface
	Given I am on the about page for "generalTest"
	When I type in the atomized name details:
		| property | value     |
		| genus    | Congea    |
		| species  | pentandra |
	And run the query
	Then the results are
		| id         | name                              |
		| kew-46552  | Congea pentandra Wall. (Synonym)  |
		| kew-234306 | Congea pentandra Steud. (Synonym) |

	Scenario: The user submits a single, unatomized query using the web interface
	Given I am on the about page for "generalTest"
	When I type in the unatomized name "Congea pentandra"
	And run the query
	Then the results are
		| id         | name                              |
		| kew-46552  | Congea pentandra Wall. (Synonym)  |
		| kew-234306 | Congea pentandra Steud. (Synonym) |
