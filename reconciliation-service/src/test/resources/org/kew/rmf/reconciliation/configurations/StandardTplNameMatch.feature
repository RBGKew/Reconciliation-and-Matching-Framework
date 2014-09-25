Feature: Reconcile against TPL, using a standard configuration (not to lax, not too rigid).

	Background:
		Given I have loaded the "standardTplNameMatch" configuration
		# Note: only names with generic epithet 'Quercus' or 'Lessingianthus' are loaded.

	Scenario: Exact match
		When I query for
			| queryId | genus   | species | authors |
			| 1       | Quercus | alba    | L.      |
		Then the results are
			| queryId | genus   | species | authors | results    |
			| 1       | Quercus | alba    | L.      | kew-171499 |

	Scenario: Wrong epithet endings
		When I query for
			| queryId | genus   | species | authors |
			| 2       | Quercus | albus   | L.      |
			| 3       | Querca  | albus   | L.      |
		Then the results are
			| queryId | genus   | species | authors | results    |
			| 2       | Quercus | albus   | L.      | kew-171499 |
			| 3       | Querca  | albus   | L.      | kew-171499 |

	Scenario: Mistaken author names
		When I query for
			| queryId | genus   | species | authors  |
			| 4       | Quercus | alba    | L.       |
		#	| 5       | Quercus | alba    | Linneus  |
		#	| 6       | Quercus | alba    | Linné    |
		Then the results are
			| queryId | genus   | species | authors | results    |
			| 4       | Quercus | alba    | L.      | kew-171499 |
		#	| 5       | Quercus | alba    | L.      | kew-171499 |
		#	| 6       | Quercus | alba    | L.      | kew-171499 |

	Scenario: Double letters
		When I query for
			| queryId | genus          | species     | authors  |
			| 7       | Lessingianthus | arctatus    | Dematt.  |
			| 8       | Lesingianthus  | arctatus    | Dematt.  |
			| 9       | Lesingianthus  | arctattus   | Dematt.  |
			| 10      | Lessingianthus | arrcttattus | Dematt.  |
		Then the results are
			| queryId | results    |
			| 7       | gcc-156539 |
			| 8       | gcc-156539 |
			| 9       | gcc-156539 |
			| 10      | gcc-156539 |

	Scenario: Single string queries
		When I query with only a single string for
			| queryId | queryString                     |
			| 41      | Quercus alba L.                 |
			| 42      | Lessingianthus arctatus Dematt. |
		Then the results are
			| queryId | results    |
			| 41      | kew-171499 |
			| 42      | gcc-156539 |
