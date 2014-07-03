Feature: Reconcile against IPNI, using a standard configuration (not to lax, not too rigid).

	Background:
		Given I have loaded the "standardIpniNameMatch" configuration

	Scenario: Exact match
		When I query for
			| queryId | genus   | species | authors |
			| 1       | Quercus | alba    | L.      |
		Then the results are
			| queryId | genus   | species | authors | results  |
			| 1       | Quercus | alba    | L.      | 295763-1 |

	Scenario: Wrong epithet endings
		When I query for
			| queryId | genus   | species | authors |
			| 2       | Quercus | albus   | L.      |
			| 3       | Querca  | albus   | L.      |
		Then the results are
			| queryId | genus   | species | authors | results  |
			| 2       | Quercus | albus   | L.      | 295763-1 |
			| 3       | Querca  | albus   | L.      | 295763-1 |

	Scenario: Mistaken author names
		When I query for
			| queryId | genus   | species | authors  |
			| 4       | Quercus | alba    | L.       |
		#	| 5       | Quercus | alba    | Linneus  |
		#	| 6       | Quercus | alba    | Linné    |
		Then the results are
			| queryId | genus   | species | authors | results  |
			| 4       | Quercus | alba    | L.      | 295763-1 |
		#	| 5       | Quercus | alba    | L.      | 295763-1 |
		#	| 6       | Quercus | alba    | L.      | 295763-1 |
