# Copyright © 2014 Royal Botanic Gardens, Kew.  See LICENSE.md for details.

Feature: Reconcile against IPNI, using a standard configuration (not to lax, not too rigid).

	Background:
		Given I have loaded the "standardIpniNameMatch" configuration
		# Note: only names in family Asteraceae are loaded for the test.

	Scenario: Exact match, at various ranks.
		When I query for
			| queryId | epithet_1      | epithet_2    | epithet_3 | rank | basionym_author | publishing_author                       | publication | collation | year |
			| 11      | Artemisia      | absinthium   |           |      |                 |                                         |             |           |      |
			| 12      | Artemisia      | absinthium   |           |      |                 | L.                                      | Sp. Pl.     | 2: 848.   | 1753 |
			| 13      | Abrotanella    | forsteroides |           |      | Hook.f.         | Benth.                                  |             |           |      |
			| 14      | Prenanthes     | abietinae    |           |      |                 | Sennikov                                |             |           |      |
			| 15      | Aaronsohnia    | pubescens    | maroccana |      | Ball            | Förther & Podlech                       |             |           |      |
			| 16      | Abrotanellinae |              |           |      |                 | H.Rob., G.D.Carr, R.M.King & A.M.Powell |             |           |      |
			| 17      | Abesina        |              |           |      |                 | Neck.                                   |             |           |      |
			| 18      | Asteraceae     |              |           |      |                 | Bercht. & J.Presl                       |             |           |      |
		Then the results are
			| queryId | results                                                  | remarks                                                  |
			| 11      | 1056033-2 179148-1 179151-1 300106-2 60439431-2 926141-1 |                                                          |
			| 12      | 179151-1 300106-2                                        |                                                          |
			| 13      | 173616-1                                                 | 52421-3 may appear at some point — inconsistency on UAT. |
			| 14      | 998885-1                                                 |                                                          |
			| 15      | 20001078-1                                               |                                                          |
			| 16      | 999406-1                                                 |                                                          |
			| 17      | 7368-1                                                   |                                                          |
			| 18      | 319342-2                                                 |                                                          |

	Scenario: Wrong epithet endings
		When I query for
			| queryId | epithet_1  | epithet_2  | publishing_author  | remark               |
			| 21      | Artemisia  | absinthia  | L.                 | Wrong epithet ending |
			| 22      | Artemisium | absinthia  | L.                 | ,,                   |
			| 23      | artemisia  | ABSINTHIUM | l.                 | Wrong case           |
		Then the results are
			| queryId | results           |
			| 21      | 179151-1 300106-2 |
			| 22      | 179151-1 300106-2 |
			| 23      | 179151-1 300106-2 |

	Scenario: Mistaken author names
		When I query for
			| queryId | epithet_1 | epithet_2  | publishing_author   |
			| 31      | Artemisia | absinthium | L.                  |
			| 32      | Artemisia | absinthium | Linnaeus            |
		Then the results are
			| queryId | results           |
			| 31      | 179151-1 300106-2 |
			| 32      | 179151-1 300106-2 |

	Scenario: Single string queries
		When I query with only a single string for
			| queryId | queryString                                                     |
			| 41      | Artemisia absinthium                                            |
			| 42      | Artemisia absinthium  L.                                        |
			| 43      | Abrotanella forsteroides (Hook.f.) Benth.                       |
			| 44      | Prenanthes abietinae Sennikov                                   |
			| 45      | Aaronsohnia pubescens subsp. maroccana (Ball) Förther & Podlech |
			| 46      | Abrotanellinae H.Rob., G.D.Carr, R.M.King & A.M.Powell          |
			| 47      | Abesina Neck.                                                   |
			| 48      | Asteraceae Bercht. & J.Presl                                    |
		Then the results are
			| queryId | results                                                  | remarks                                                  |
			| 41      | 1056033-2 179148-1 179151-1 300106-2 60439431-2 926141-1 |                                                          |
			| 42      | 179151-1 300106-2                                        |                                                          |
			| 43      | 173616-1                                                 | 52421-3 may appear at some point — inconsistency on UAT. |
			| 44      | 998885-1                                                 |                                                          |
			| 45      | 20001078-1                                               |                                                          |
			| 46      | 999406-1                                                 |                                                          |
			| 47      | 7368-1                                                   |                                                          |
			| 48      | 319342-2                                                 |                                                          |
