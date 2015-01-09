# Copyright © 2014 Royal Botanic Gardens, Kew.  See LICENSE.md for details.

Feature: Matches should return a score, which the user can use to see which is the closer match.

	Background:
		Given I have loaded the "generalTestMatch" configuration

	Scenario: Score matches appropriately.
		When I query for
			| queryId | genus    | species        | authors    |
			| 1       | Congea   | griffithiana   | Munir      |
			| 2       | x Congea | griffithiana   | Munir      |
			| 3       | Congeus  | griffithiana   | Munir      |
			| 4       | Congea   | griffi-thiana  | Munirstuff |
			| 5       | Congea   | gri-ffi-thiana | Munirstuff |
		Then the results are
			| queryId | genus  | species      | authors | results           |
			| 1       | Congea | griffithiana | Munir   | kew-46543[1.0000] |
			| 2       | Congea | griffithiana | Munir   | kew-46543[0.9833] |
			| 3       | Congea | griffithiana | Munir   | kew-46543[0.8333] |
			| 4       | Congea | griffithiana | Munir   | kew-46543[0.8333] |
			| 5       | Congea | griffithiana | Munir   | kew-46543[0.6667] |
