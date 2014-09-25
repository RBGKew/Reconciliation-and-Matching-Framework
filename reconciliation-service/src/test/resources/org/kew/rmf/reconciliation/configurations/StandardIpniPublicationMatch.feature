Feature: Reconcile against IPNI Publications, using a standard configuration (not to lax, not too rigid).

	Background:
		Given I have loaded the "standardIpniPublicationMatch" configuration

	Scenario: Simple exact match
		When I query for
			| queryId | publication              | author | date |
			| 11      | Ann. Missouri Bot. Gard. |        | 1915 |
		Then the results are
			| queryId | publication              | author | date           | results |
			| 11      | Ann. Missouri Bot. Gard. |        | Vol. 1+, 1914+ | 1-2     |

	Scenario: Close match
		When I query for
			| queryId | publication | author | date |
			| 21      | Fl. Ind.    | Hooker | 1855 |
		Then the results are
			| queryId | publication                    | author                                | date                                                                        | results |
			| 21      | Fl. Ind. [Hooker f. & Thomson] | Hooker, Joseph Dalton²Thomson, Thomas | 1-19 Jul 1855; only one volume published; replace by¹Flora of British India | 4340-2  |
