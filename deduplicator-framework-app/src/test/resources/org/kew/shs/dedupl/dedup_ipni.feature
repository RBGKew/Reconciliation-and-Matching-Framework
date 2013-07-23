# This is a very high-level test with real domain-specific data.
# It should maybe not live within the application?
Feature: Deduplicate Ipni
	As a DataImprover
	In order to deduplicate Ipni
	I want the deduplicator framework to do the work for me, I just have to provide a decent configuration.

	Scenario: Genus level
		Given Rachel has created an input-file to feed the deduplicator framework containing tab-separated Genus data
			| id         | family      | genus           | authors     | basionym_author | publishing_author | full_name_without_family_and_authors | publication              | collation     | publication_year | reference_remarks | remarks | std_score | test_concern                                                                                      |
			| 30022170-2 | Rapateaceae | Saxo-fridericia | R.H.Schomb. |                 | R.H.Schomb.       | Saxo-fridericia                      | Rapatea                  | 13            | 1845             |                   |         | 9         | Does presence of hyphen in genus name prevent clustering                                          |
			| 1001-1     | Ericaceae   | Leucothoë       | D.Don       |                 | D.Don             | Leucothoë                            | Edinburgh New Philos. J. | 17: 159       | 1834             |                   |         | 9         | Does presence of diacritic character at end of name prevent clustering                            |
			| 1001-2     | Ericaceae   | Leucothoë       | D.Don       |                 | D.Don             | Leucothoë                            | Edinburgh New Philos. J. | 17: 159       | 1834             |                   |         | 9         | Does presence of diacritic character at end of name prevent clustering                            |
			| 2001-1     | Ericaceae   | Lëucothoe       | D.Don       |                 | D.Don             | Lëucothoe                            | Edinburgh New Philos. J. | 17: 158       | 1834             |                   |         | 9         | Does presence of diacritic character in middle of name prevent clustering                         |
			| 2001-2     | Ericaceae   | Lëucothoe       | D.Don       |                 | D.Don             | Lëucothoe                            | Edinburgh New Philos. J. | 17: 158       | 1834             |                   |         | 9         | Does presence of diacritic character in middle of name prevent clustering                         |
			| 6001-1     | Ericaceae   | Leucothoe       | D.Dön       |                 | D.Dön             | Leucothoe                            | Edinburgh New Philos. J. | 16: 158       | 1834             |                   |         | 9         | Does presence of diacritic character in middle of author prevent clustering                       |
			| 6001-2     | Ericaceae   | Leucothoe       | D.Dön       |                 | D.Dön             | Leucothoe                            | Edinburgh New Philos. J. | 16: 158       | 1834             |                   |         | 9         | Does presence of diacritic character in middle of author prevent clustering                       |
			| 7001-1     | Ericaceae   | Leucothoe       | D.Donë      |                 | D.Donë            | Leucothoe                            | Edinburgh New Philos. J. | 15: 158       | 1834             |                   |         | 9         | Does presence of diacritic character at end of author prevent clustering                          |
			| 7001-2     | Ericaceae   | Leucothoe       | D.Donë      |                 | D.Donë            | Leucothoe                            | Edinburgh New Philos. J. | 15: 158       | 1834             |                   |         | 9         | Does presence of diacritic character at end of author prevent clustering                          |
			| 33288-1    | Rapateaceae | Saxo-fridericia | R.H.Schomb. |                 | R.H.Schomb.       | Saxo-fridericia                      | Rapatea                  | 13            | 1845             |                   |         | 8         | Does presence of hyphen in genus name prevent clustering                                          |
			| 28674-1    | Orchidaceae | Amphorchis      | Thouars     |                 | Thouars           | Amphorchis                           | Hist. Orchid.            | Tabl. Synopt. | 1822             |                   |         | 8         | Does absence of numerical characters in genus name prevent clustering                             |
			| 3001-1     | Orchidaceae | × Amphorchis    | Thouars     |                 | Thouars           | × Amphorchis                         | Hist. Orchid.            | 211           | 1822             |                   |         | 8         | Does presence of multiplication sign and whitespace at start of genus name prevent clustering     |
			| 3001-2     | Orchidaceae | × Amphorchis    | Thouars     |                 | Thouars           | × Amphorchis                         | Hist. Orchid.            | 211           | 1822             |                   |         | 8         | Does presence of multiplication sign and whitespace at start of genus name prevent clustering     |
			| 4001-1     | Orchidaceae | ×Amphorchis     | Thouars     |                 | Thouars           | ×Amphorchis                          | Hist. Orchid.            | 56            | 1822             |                   |         | 8         | Does presence of multiplication sign without whitespace at start of genus name prevent clustering |
			| 4001-2     | Orchidaceae | ×Amphorchis     | Thouars     |                 | Thouars           | ×Amphorchis                          | Hist. Orchid.            | 56            | 1822             |                   |         | 8         | Does presence of multiplication sign without whitespace at start of genus name prevent clustering |
			| 5001-1     | Orchidaceae | X Amphorchis    | Thouars     |                 | Thouars           | X Amphorchis                         | Hist. Orchid.            | 11            | 1822             |                   |         | 8         | Does presence of capital letter X at start of name prevent clustering                             |
			| 5001-2     | Orchidaceae | X Amphorchis    | Thouars     |                 | Thouars           | X Amphorchis                         | Hist. Orchid.            | 11            | 1822             |                   |         | 8         | Does presence of capital letter X at start of name prevent clustering                             |
			| 9999-1     | Rapateaceae | Saxo-fridericia | R.H.Schomb. |                 | R.H.Schomb.       | Saxo-fridericia                      | Rapatea                  | 13            | 1845             |                   |         | 7         | Does presence of hyphen in genus name prevent clustering                                          |
			| 28675-1    | Orchidaceae | Amphorchis      | Thouars     |                 | Thouars           | Amphorchis                           | Hist. Orchid.            | Tabl. Synopt. | 1822             |                   |         | 7         | Does absence of numerical characters in genus name prevent clustering                             |

		And Alecs has set up a genus-dedup configuration file according to her specs:
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:util="http://www.springframework.org/schema/util"
                xmlns:p="http://www.springframework.org/schema/p"
                xmlns:c="http://www.springframework.org/schema/c"
                xsi:schemaLocation="
                    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
                    http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.1.xsd">
                <bean id="preferencePlaceHolder" class="org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer">
                    <property name="locations">
                        <list>
                        </list>
                    </property>
                </bean>
                <bean id="lucene_directory" class="java.lang.String">
                    <constructor-arg value="target/deduplicator"/>
                </bean>
                <bean id="inputfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/input.txt" />
                </bean>
                <bean id="outputfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/output.txt" />
                </bean>
                <bean id="reportfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/REPLACE_REPORTFILE" />
                </bean>
                <bean id="topcopyfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/REPLACE_TOPCOPYFILE" />
                </bean>
                <bean id="exactMatcher" class="org.kew.shs.dedupl.matchers.ExactMatcher" />
                <bean id="authorCommonTokensMatcher" class="org.kew.shs.dedupl.matchers.AuthorCommonTokensMatcher"
                    p:minRatio="0.5"/>
                <bean id="capitalLettersMatcher" class="org.kew.shs.dedupl.matchers.CapitalLettersMatcher"
                    p:minRatio="0.5"/>
                <bean id="numberMatcher" class="org.kew.shs.dedupl.matchers.NumberMatcher"
                    p:minRatio="0.5"/>
                <bean id="alwaysMatchingMatcher" class="org.kew.shs.dedupl.matchers.AlwaysMatchingMatcher" />
                <bean id="yearCleaner"
                    class="org.kew.shs.dedupl.transformers.ZeroToBlankTransformer" />
                <bean id="safeStripNonAlphasTransformer"
                    class="org.kew.shs.dedupl.transformers.SafeStripNonAlphasTransformer" />
                <bean id="fakeHybridSignCleaner"
                    class="org.kew.shs.dedupl.transformers.FakeHybridSignCleaner" />
                <bean id="safeStripNonAlphaNumericsTransformer"
                    class="org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer" />
                <bean id="collationCleaner"
                    class="org.kew.shs.dedupl.transformers.CompositeTransformer">
                    <property name="transformers">
                        <util:list id="1">
                            <bean id="icollationCleaner"
                                class="org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer" />
                            <bean id="rcollationCleaner"
                                class="org.kew.shs.dedupl.transformers.RomanNumeralTransformer" />
                        </util:list>
                    </property>
                </bean>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="family"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="genus"/>
                        <property name="matcher" ref="exactMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="fakeHybridSignCleaner"/>
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="useInSelect" value="true"/>
                        <property name="indexInitial" value="true"/>
                        <property name="indexLength" value="true"/>
                        <property name="indexOriginal" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="basionym_author"/>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="publishing_author"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="indexOriginal" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="full_name_without_family_and_authors"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="publication"/>
                        <property name="matcher" ref="capitalLettersMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="collation"/>

                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="collationCleaner"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="numberMatcher"/>
                        <property name="indexOriginal" value="true"/>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="publication_year"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="blanksMatch" value="true"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="yearCleaner"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="reference_remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="std_score"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="test_concern"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="inputfile"
                    p:sourceFileDelimiter="&#09;"
                    p:outputFileDelimiter="&#09;"
                    p:outputFileIdDelimiter=","
                    p:outputFile-ref="outputfile"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:reportFile-ref="reportfile"
                    p:topCopyFile-ref="topcopyfile"
                    p:scoreFieldName="std_score"/>
            </beans>
            """
		When this genus config is run through the Dedupl App
		Then a file should have been created in the same folder with the following genus data:
            | id         | family      | genus_orig      | genus           | genus_length | genus_init | basionym_author | publishing_author_orig | publishing_author | full_name_without_family_and_authors | publication              | collation_orig | collation     | publication_year | reference_remarks | remarks | std_score | test_concern                                                                                      | cluster_size | from_id    | ids_in_cluster                  |
            | 30022170-2 | Rapateaceae | Saxo-fridericia | Saxo fridericia | 15           | S          |                 | R.H.Schomb.            | R H Schomb        | Saxo-fridericia                      | Rapatea                  | 13             | 13            | 1845             |                   |         | 9         | Does presence of hyphen in genus name prevent clustering                                          | 3            | 30022170-2 | 30022170-2 \| 33288-1 \| 9999-1 |
            | 1001-1     | Ericaceae   | Leucothoë       | Leucothoe       | 09           | L          |                 | D.Don                  | D Don             | Leucothoë                            | Edinburgh New Philos J   | 17: 159        | 17 159        | 1834             |                   |         | 9         | Does presence of diacritic character at end of name prevent clustering                            | 2            | 1001-1     | 1001-1 \| 1001-2                |
            | 2001-1     | Ericaceae   | Lëucothoe       | Leucothoe       | 09           | L          |                 | D.Don                  | D Don             | Lëucothoe                            | Edinburgh New Philos J   | 17: 158        | 17 158        | 1834             |                   |         | 9         | Does presence of diacritic character in middle of name prevent clustering                         | 2            | 2001-1     | 2001-1 \| 2001-2                |
            | 6001-1     | Ericaceae   | Leucothoe       | Leucothoe       | 09           | L          |                 | D.Dön                  | D Don             | Leucothoe                            | Edinburgh New Philos J   | 16: 158        | 16 158        | 1834             |                   |         | 9         | Does presence of diacritic character in middle of author prevent clustering                       | 2            | 6001-1     | 6001-1 \| 6001-2                |
            | 7001-1     | Ericaceae   | Leucothoe       | Leucothoe       | 09           | L          |                 | D.Donë                 | D Done            | Leucothoe                            | Edinburgh New Philos J   | 15: 158        | 15 158        | 1834             |                   |         | 9         | Does presence of diacritic character at end of author prevent clustering                          | 2            | 7001-1     | 7001-1 \| 7001-2                |
            | 28674-1    | Orchidaceae | Amphorchis      | Amphorchis      | 10           | A          |                 | Thouars                | Thouars           | Amphorchis                           | Hist Orchid              | Tabl. Synopt.  | Tabl Synopt   | 1822             |                   |         | 8         | Does absence of numerical characters in genus name prevent clustering                             | 2            | 28674-1    | 28674-1 \| 28675-1              |
            | 3001-1     | Orchidaceae | × Amphorchis    | Amphorchis      | 10           | A          |                 | Thouars                | Thouars           | × Amphorchis                         | Hist Orchid              | 211            | 211           | 1822             |                   |         | 8         | Does presence of multiplication sign and whitespace at start of genus name prevent clustering     | 2            | 3001-1     | 3001-1 \| 3001-2                |
            | 4001-1     | Orchidaceae | ×Amphorchis     | Amphorchis      | 10           | A          |                 | Thouars                | Thouars           | ×Amphorchis                          | Hist Orchid              | 56             | 56            | 1822             |                   |         | 8         | Does presence of multiplication sign without whitespace at start of genus name prevent clustering | 2            | 4001-1     | 4001-1 \| 4001-2                |
            | 5001-1     | Orchidaceae | X Amphorchis    | Amphorchis      | 10           | A          |                 | Thouars                | Thouars           | X Amphorchis                         | Hist Orchid              | 11             | 11            | 1822             |                   |         | 8         | Does presence of capital letter X at start of name prevent clustering                             | 2            | 5001-1     | 5001-1 \| 5001-2                |


	Scenario: Species level
		Given Eszter has created an input-file to feed the deduplicator framework containing tab-separated Species data
            |         id |          family |       genus |  rank | basionym_author |                   publishing_author | full_name_without_family_and_authors |                       publication |            collation | publication_year | reference_remarks |             remarks | std_score |      dit_family |                                                                                                                                           test_concern |
            |   307089-2 |        Fabaceae |    Baptisia | spec. |                 | Kosnik, Diggs, Redshaw & Lipscomb   |                Baptisia × variicolor |                              Sida |              17: 498 |             1996 |                   |                     |        13 |     Leguminosae |                                                  Does presence of multiplication sign and whitespace between genus and species name prevent clustering |
            |   300547-2 |     Orchidaceae |    Barkeria | spec. |                 |                         Soto Arenas |         Barkeria fritz-halbingeriana |            Orquidea (Mexico City) | 13: 245 (-246; fig.) |             1993 |                   |                     |        13 |     Orchidaceae |                                                                                          Does presence of hyphen in species epithet prevent clustering |
            |   300068-2 | Dryopteridaceae | Dryostichum | spec. |                 |                          W.H.Wagner |              × Dryostichum singulare |                    Canad. J. Bot. |      "70: 248, figs" |             1992 |                   |                     |        12 | Dryopteridaceae |                                                          Does presence of multiplication sign and whitespace at start of genus name prevent clustering |
            |   312304-2 |     Orchidaceae |    Barkeria | spec. |                 |                         Soto Arenas |         Barkeria fritz-halbingeriana |            Orquidea (Mexico City) |      13: 245-246 fig |             1993 |                   |                     |        12 |     Orchidaceae |                                                                                          Does presence of hyphen in species epithet prevent clustering |
            |   319079-2 |     Begoniaceae |     Begonia | spec. |                 |                                  L. |                      Begonia obliqua |                           Sp. Pl. |              2: 1056 |             1753 |                   | [Gandhi 1 Dec 1999] |        12 |     Begoniaceae |                                                                                Will these be clustered now? First attempt didn't clustered these names |
            |     1001-2 |     Begoniaceae |     Begonia | spec. |                 |                                  L. |                      Begonia obliqua |                           Sp. Pl. |              2: 1056 |             1753 |                   | [Gandhi 1 Dec 1999] |        12 |     Begoniaceae |                                                                                Will these be clustered now? First attempt didn't clustered these names |
            |   289660-2 |     Orchidaceae |     Dracula | spec. |                 |                                Luer |           Dracula × radio-syndactyla |                          Selbyana |         5: 389(-390) |             1981 |                   |                     |        12 |     Orchidaceae |                    Does presence of multiplication sign and whitespace between genus and species name and hyphen in species epithet prevent clustering |
            |   993921-1 |     Leguminosae |    Baptisia | spec. |                 | Kosnik, Diggs, Redshaw & Lipscomb   |                Baptisia × variicolor |                              Sida |           17(2): 498 |             1996 |                   |                     |        11 |     Leguminosae |                                                  Does presence of multiplication sign and whitespace between genus and species name prevent clustering |
            |    30810-2 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                      Begonia peltata |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |        11 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |     1003-2 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                      Begonia peltata |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |        11 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |    30866-2 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                       Begonia repens |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |        11 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |     1005-2 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                       Begonia repens |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |        11 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |    31516-2 |   Berberidaceae |    Berberis | spec. |                 |                        Sessé & Moc. |                     Berberis pinnata |               "Fl. Mexic., ed. 2" |                   84 |             1894 |                   |                     |        11 |   Berberidaceae |                                                  "These names got clustered for first attempt, cannot see difference between these and Begonia repens" |
            |     1007-2 |   Berberidaceae |    Berberis | spec. |                 |                        Sessé & Moc. |                     Berberis pinnata |               "Fl. Mexic., ed. 2" |                   84 |             1894 |                   |                     |        11 |   Berberidaceae |                                                  "These names got clustered for first attempt, cannot see difference between these and Begonia repens" |
            |   139242-2 |       Ericaceae |   Leucothoë | spec. |                 |                             Sleumer |                 Leucothoë columbiana | Notizbl. Bot. Gart. Berlin-Dahlem |              12: 479 |             1935 |                   |                     |        11 |       Ericaceae |                                                                           Does presence of diacritic character at end of genus name prevent clustering |
            |   897642-1 |     Orchidaceae |     Dracula | spec. |                 |                                Luer |           Dracula × radio-syndactyla |                          Selbyana |        5: 389 (1981) |             1981 |                   |                     |        10 |     Orchidaceae |                    Does presence of multiplication sign and whitespace between genus and species name and hyphen in species epithet prevent clustering |
            |   105293-1 |     Begoniaceae |     Begonia | spec. |                 |                                  L. |                      Begonia obliqua |                           Sp. Pl. |              2: 1056 |             1753 |                   |                     |         9 |     Begoniaceae |                                                                                Will these be clustered now? First attempt didn't clustered these names |
            |     1002-1 |     Begoniaceae |     Begonia | spec. |                 |                                  L. |                      Begonia obliqua |                           Sp. Pl. |              2: 1056 |             1753 |                   |                     |         9 |     Begoniaceae |                                                                                Will these be clustered now? First attempt didn't clustered these names |
            |   105394-1 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                      Begonia peltata |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |         9 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |     1004-1 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                      Begonia peltata |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |         9 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |   105568-1 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                       Begonia repens |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |         9 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |     1006-1 |     Begoniaceae |     Begonia | spec. |                 |                        Sessé & Moc. |                       Begonia repens |               "Fl. Mexic., ed. 2" |                  219 |             1894 |                   |                     |         9 |     Begoniaceae | Does presence of diacritic character at end of author prevent clustering also these names didn't cluster for the first attempt (cannot see difference) |
            |   106995-1 |   Berberidaceae |    Berberis | spec. |                 |                        Sessé & Moc. |                     Berberis pinnata |               "Fl. Mexic., ed. 2" |                   84 |             1894 |                   |                     |         9 |   Berberidaceae |                                                  "These names got clustered for first attempt, cannot see difference between these and Begonia repens" |
            |     1008-1 |   Berberidaceae |    Berberis | spec. |                 |                        Sessé & Moc. |                     Berberis pinnata |               "Fl. Mexic., ed. 2" |                   84 |             1894 |                   |                     |         9 |   Berberidaceae |                                                  "These names got clustered for first attempt, cannot see difference between these and Begonia repens" |
            |   331017-1 |       Ericaceae |   Leucothoë | spec. |                 |                             Sleumer |                 Leucothoë columbiana | Notizbl. Bot. Gart. Berlin-Dahlem |              12: 479 |             1935 |                   |                     |         9 |       Ericaceae |                                                                           Does presence of diacritic character at end of genus name prevent clustering |
            | 17542760-1 | Dryopteridaceae | Dryostichum | spec. |                 |                          W.H.Wagner |              × Dryostichum singulare |                    Canad. J. Bot. |    70(2): 248 (1992) |                0 |                   |                     |         8 | Dryopteridaceae |                                                          Does presence of multiplication sign and whitespace at start of genus name prevent clustering |

		And Alecs has set up a species-dedup configuration file in the same folder according to her specs:
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:util="http://www.springframework.org/schema/util"
                xmlns:p="http://www.springframework.org/schema/p"
                xmlns:c="http://www.springframework.org/schema/c"
                xsi:schemaLocation="
                    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
                    http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.1.xsd">
                <bean id="preferencePlaceHolder" class="org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer">
                    <property name="locations">
                        <list>
                        </list>
                    </property>
                </bean>
                <bean id="lucene_directory" class="java.lang.String">
                    <constructor-arg value="target/deduplicator"/>
                </bean>
                <bean id="inputfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/input.txt" />
                </bean>
                <bean id="outputfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/output.txt" />
                </bean>
                <bean id="reportfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/REPLACE_REPORTFILE" />
                </bean>
                <bean id="topcopyfile" class="java.io.File">
                    <constructor-arg value="${NMT_DATADIR}/REPLACE_TOPCOPYFILE" />
                </bean>
                <bean id="exactMatcher" class="org.kew.shs.dedupl.matchers.ExactMatcher" />
                <bean id="authorCommonTokensMatcher" class="org.kew.shs.dedupl.matchers.AuthorCommonTokensMatcher"
                    p:minRatio="0.5"/>
                <bean id="capitalLettersMatcher" class="org.kew.shs.dedupl.matchers.CapitalLettersMatcher"
                    p:minRatio="0.5"/>
                <bean id="numberMatcher" class="org.kew.shs.dedupl.matchers.NumberMatcher"
                    p:minRatio="0.5"/>
                <bean id="alwaysMatchingMatcher" class="org.kew.shs.dedupl.matchers.AlwaysMatchingMatcher" />
                <bean id="yearCleaner"
                    class="org.kew.shs.dedupl.transformers.ZeroToBlankTransformer" />
                <bean id="safeStripNonAlphasTransformer"
                    class="org.kew.shs.dedupl.transformers.SafeStripNonAlphasTransformer" />
                <bean id="fakeHybridSignCleaner"
                    class="org.kew.shs.dedupl.transformers.FakeHybridSignCleaner" />
                <bean id="safeStripNonAlphaNumericsTransformer"
                    class="org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer" />
                <bean id="collationCleaner"
                    class="org.kew.shs.dedupl.transformers.CompositeTransformer">
                    <property name="transformers">
                        <util:list id="1">
                            <bean id="icollationCleaner"
                                class="org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer" />
                            <bean id="rcollationCleaner"
                                class="org.kew.shs.dedupl.transformers.RomanNumeralTransformer" />
                        </util:list>
                    </property>
                </bean>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="family" />
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="genus"/>
                        <property name="matcher" ref="exactMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="fakeHybridSignCleaner"/>
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="useInSelect" value="true"/>
                        <property name="indexInitial" value="true"/>
                        <property name="indexLength" value="true"/>
                        <property name="indexOriginal" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="basionym_author"/>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="publishing_author"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="indexOriginal" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="full_name_without_family_and_authors"/>
                        <property name="matcher" ref="exactMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="fakeHybridSignCleaner"/>
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="indexOriginal" value="true"/>
                        <property name="useInSelect" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="publication"/>
                        <property name="matcher" ref="capitalLettersMatcher"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
		                        <ref bean="safeStripNonAlphaNumericsTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="collation"/>
                        <property name="lookupTransformers">
                            <util:list id="1">
		                        <ref bean="collationCleaner"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="numberMatcher"/>
                        <property name="indexOriginal" value="true"/>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="publication_year"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="blanksMatch" value="true" />
						<property name="lookupTransformers">
                            <util:list id="1">
		                        <ref bean="collationCleaner"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="reference_remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="std_score"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="name" value="dit_family"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="inputfile"
                    p:sourceFileDelimiter="&#09;"
                    p:outputFileDelimiter="&#09;"
                    p:outputFileIdDelimiter=","
                    p:outputFile-ref="outputfile"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:reportFile-ref="reportfile"
                    p:topCopyFile-ref="topcopyfile"
                    p:scoreFieldName="std_score"/>
            </beans>
            """
		When this species config is run through the Dedupl App
		Then a file should have been created in the same folder with the following species data:
            |       id |          family |  genus_orig |       genus | genus_length | genus_init | basionym_author |                   publishing_author_orig |             publishing_author | full_name_without_family_and_authors_orig | full_name_without_family_and_authors |                    publication |       collation_orig |      collation | publication_year | reference_remarks |             remarks | std_score |      dit_family | cluster_size |  from_id |                           ids_in_cluster |
            | 307089-2 |        Fabaceae |    Baptisia |    Baptisia |           08 |          B |                 |      Kosnik, Diggs, Redshaw & Lipscomb   | Kosnik Diggs Redshaw Lipscomb |                     Baptisia × variicolor |                  Baptisia variicolor |                           Sida |              17: 498 |         17 498 |             1996 |                   |                     |        13 |     Leguminosae |            2 | 307089-2 |                     307089-2 \| 993921-1 |
            | 300547-2 |     Orchidaceae |    Barkeria |    Barkeria |           08 |          B |                 |                              Soto Arenas |                   Soto Arenas |              Barkeria fritz-halbingeriana |         Barkeria fritz halbingeriana |           Orquidea Mexico City | 13: 245 (-246; fig.) | 13 245 246 fig |             1993 |                   |                     |        13 |     Orchidaceae |            2 | 300547-2 |                     300547-2 \| 312304-2 |
            | 300068-2 | Dryopteridaceae | Dryostichum | Dryostichum |           11 |          D |                 |                               W.H.Wagner |                    W H Wagner |                   × Dryostichum singulare |                Dryostichum singulare |                    Canad J Bot |      70: 248, figs   |    70 248 figs |             1992 |                   |                     |        12 | Dryopteridaceae |            2 | 300068-2 |                   300068-2 \| 17542760-1 |
            | 319079-2 |     Begoniaceae |     Begonia |     Begonia |           07 |          B |                 |                                       L. |                             L |                           Begonia obliqua |                      Begonia obliqua |                          Sp Pl |              2: 1056 |         2 1056 |             1753 |                   | [Gandhi 1 Dec 1999] |        12 |     Begoniaceae |            4 | 319079-2 | 319079-2 \| 1001-2 \| 105293-1 \| 1002-1 |
            | 289660-2 |     Orchidaceae |     Dracula |     Dracula |           07 |          D |                 |                                     Luer |                          Luer |                Dracula × radio-syndactyla |             Dracula radio syndactyla |                       Selbyana |         5: 389(-390) |      5 389 390 |             1981 |                   |                     |        12 |     Orchidaceae |            2 | 289660-2 |                     289660-2 \| 897642-1 |
            |  30810-2 |     Begoniaceae |     Begonia |     Begonia |           07 |          B |                 |                             Sessé & Moc. |                     Sesse Moc |                           Begonia peltata |                      Begonia peltata |                  Fl Mexic ed 2 |                  219 |            219 |             1894 |                   |                     |        11 |     Begoniaceae |            4 |  30810-2 |  30810-2 \| 1003-2 \| 105394-1 \| 1004-1 |
            |  30866-2 |     Begoniaceae |     Begonia |     Begonia |           07 |          B |                 |                             Sessé & Moc. |                     Sesse Moc |                            Begonia repens |                       Begonia repens |                  Fl Mexic ed 2 |                  219 |            219 |             1894 |                   |                     |        11 |     Begoniaceae |            4 |  30866-2 |  30866-2 \| 1005-2 \| 105568-1 \| 1006-1 |
            |  31516-2 |   Berberidaceae |    Berberis |    Berberis |           08 |          B |                 |                             Sessé & Moc. |                     Sesse Moc |                          Berberis pinnata |                     Berberis pinnata |                  Fl Mexic ed 2 |                   84 |             84 |             1894 |                   |                     |        11 |   Berberidaceae |            4 |  31516-2 |  31516-2 \| 1007-2 \| 106995-1 \| 1008-1 |
            | 139242-2 |       Ericaceae |   Leucothoë |   Leucothoe |           09 |          L |                 |                                  Sleumer |                       Sleumer |                      Leucothoë columbiana |                 Leucothoe columbiana | Notizbl Bot Gart Berlin Dahlem |              12: 479 |         12 479 |             1935 |                   |                     |        11 |       Ericaceae |            2 | 139242-2 |                     139242-2 \| 331017-1 |

