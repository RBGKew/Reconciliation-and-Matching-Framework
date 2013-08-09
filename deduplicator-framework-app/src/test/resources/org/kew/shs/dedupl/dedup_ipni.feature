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
                    <constructor-arg value="REPLACE_WITH_TMPDIR/input.txt" />
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
                <util:list id="reporters">
                    <bean class="org.kew.shs.dedupl.reporters.DedupReporter"
                        p:name="dedupReporter"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/output.txt" />
                            </bean>
                        </property>
                    </bean>
                </util:list>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="family"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="genus"/>
                        <property name="matcher" ref="exactMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="fakeHybridSignCleaner"/>
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="useInSelect" value="true"/>
                        <property name="indexInitial" value="true"/>
                        <property name="indexLength" value="true"/>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="basionym_author"/>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="publishing_author"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="full_name_without_family_and_authors"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="publication"/>
                        <property name="matcher" ref="capitalLettersMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="collation"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="collationCleaner"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="numberMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="blanksMatch" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="publication_year"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="blanksMatch" value="true"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="yearCleaner"/>
                            </util:list>
                        </property>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="reference_remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="std_score"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="test_concern"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="inputfile"
                    p:sourceFileDelimiter="&#09;"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:scoreFieldName="std_score"
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the deduplication-specific bit -->
                <import resource="classpath*:application-context-dedup.xml"/>
            </beans>
            """
        When this genus config is run through the Dedupl App
        Then a file should have been created in the same folder with the following genus data:
            | id         | family      | genus           | genus_transf    | publishing_author      | publishing_author_transf | full_name_without_family_and_authors | publication_transf       | collation      | collation_transf | publication_year | std_score | test_concern                                                                                      | cluster_size | from_id    | ids_in_cluster                  |
            | 30022170-2 | Rapateaceae | Saxo-fridericia | Saxo fridericia | R.H.Schomb.            | R H Schomb               | Saxo-fridericia                      | Rapatea                  | 13             | 13               | 1845             | 9         | Does presence of hyphen in genus name prevent clustering                                          | 3            | 30022170-2 | 30022170-2 \| 33288-1 \| 9999-1 |
            | 1001-1     | Ericaceae   | Leucothoë       | Leucothoe       | D.Don                  | D Don                    | Leucothoë                            | Edinburgh New Philos J   | 17: 159        | 17 159           | 1834             | 9         | Does presence of diacritic character at end of name prevent clustering                            | 2            | 1001-1     | 1001-1 \| 1001-2                |
            | 2001-1     | Ericaceae   | Lëucothoe       | Leucothoe       | D.Don                  | D Don                    | Lëucothoe                            | Edinburgh New Philos J   | 17: 158        | 17 158           | 1834             | 9         | Does presence of diacritic character in middle of name prevent clustering                         | 2            | 2001-1     | 2001-1 \| 2001-2                |
            | 6001-1     | Ericaceae   | Leucothoe       | Leucothoe       | D.Dön                  | D Don                    | Leucothoe                            | Edinburgh New Philos J   | 16: 158        | 16 158           | 1834             | 9         | Does presence of diacritic character in middle of author prevent clustering                       | 2            | 6001-1     | 6001-1 \| 6001-2                |
            | 7001-1     | Ericaceae   | Leucothoe       | Leucothoe       | D.Donë                 | D Done                   | Leucothoe                            | Edinburgh New Philos J   | 15: 158        | 15 158           | 1834             | 9         | Does presence of diacritic character at end of author prevent clustering                          | 2            | 7001-1     | 7001-1 \| 7001-2                |
            | 28674-1    | Orchidaceae | Amphorchis      | Amphorchis      | Thouars                | Thouars                  | Amphorchis                           | Hist Orchid              | Tabl. Synopt.  | Tabl Synopt      | 1822             | 8         | Does absence of numerical characters in genus name prevent clustering                             | 2            | 28674-1    | 28674-1 \| 28675-1              |
            | 3001-1     | Orchidaceae | × Amphorchis    | Amphorchis      | Thouars                | Thouars                  | × Amphorchis                         | Hist Orchid              | 211            | 211              | 1822             | 8         | Does presence of multiplication sign and whitespace at start of genus name prevent clustering     | 2            | 3001-1     | 3001-1 \| 3001-2                |
            | 4001-1     | Orchidaceae | ×Amphorchis     | Amphorchis      | Thouars                | Thouars                  | ×Amphorchis                          | Hist Orchid              | 56             | 56               | 1822             | 8         | Does presence of multiplication sign without whitespace at start of genus name prevent clustering | 2            | 4001-1     | 4001-1 \| 4001-2                |
            | 5001-1     | Orchidaceae | X Amphorchis    | Amphorchis      | Thouars                | Thouars                  | X Amphorchis                         | Hist Orchid              | 11             | 11               | 1822             | 8         | Does presence of capital letter X at start of name prevent clustering                             | 2            | 5001-1     | 5001-1 \| 5001-2                |


    Scenario: Species level
        Given Eszter has created an input-file to feed the deduplicator framework containing tab-separated Species data
            |         id |          family |       genus |  rank | basionym_author |                   publishing_author | full_name_without_family_and_authors |                       publication |            collation | publication_year | reference_remarks |             remarks | std_score |      dit_family |                                                                                                                                           test_concern |
            |   307089-2 |        Fabaceae |    Baptisia | spec. |                 |   Kosnik, Diggs, Redshaw & Lipscomb |                Baptisia × variicolor |                              Sida |              17: 498 |             1996 |                   |                     |        13 |     Leguminosae |                                                  Does presence of multiplication sign and whitespace between genus and species name prevent clustering |
            |   300547-2 |     Orchidaceae |    Barkeria | spec. |                 |                         Soto Arenas |         Barkeria fritz-halbingeriana |            Orquidea (Mexico City) | 13: 245 (-246; fig.) |             1993 |                   |                     |        13 |     Orchidaceae |                                                                                          Does presence of hyphen in species epithet prevent clustering |
            | 30345915-2 | Dryopteridaceae |  Dryopteris | spec. |                 |                              Schott |                 Dryopteris filix-mas |                Gen. Fil. [Schott] |                   67 |              1834|                   |                     |        13 | Dryopteridaceae |                                                                                                    Does hybrid epithet cluster with non-hybrid epithet |
            |   300068-2 | Dryopteridaceae | Dryostichum | spec. |                 |                          W.H.Wagner |              × Dryostichum singulare |                    Canad. J. Bot. |      "70: 248, figs" |             1992 |                   |                     |        12 | Dryopteridaceae |                                                          Does presence of multiplication sign and whitespace at start of genus name prevent clustering |
            |   312304-2 |     Orchidaceae |    Barkeria | spec. |                 |                         Soto Arenas |         Barkeria fritz-halbingeriana |            Orquidea (Mexico City) |      13: 245-246 fig |             1993 |                   |                     |        12 |     Orchidaceae |                                                                                          Does presence of hyphen in species epithet prevent clustering |
            |   319079-2 |     Begoniaceae |     Begonia | spec. |                 |                                  L. |                      Begonia obliqua |                           Sp. Pl. |              2: 1056 |             1753 |                   | [Gandhi 1 Dec 1999] |        12 |     Begoniaceae |                                                                                Will these be clustered now? First attempt didn't clustered these names |
            |     1001-2 |     Begoniaceae |     Begonia | spec. |                 |                                  L. |                      Begonia obliqua |                           Sp. Pl. |              2: 1056 |             1753 |                   | [Gandhi 1 Dec 1999] |        12 |     Begoniaceae |                                                                                Will these be clustered now? First attempt didn't clustered these names |
            |   289660-2 |     Orchidaceae |     Dracula | spec. |                 |                                Luer |           Dracula × radio-syndactyla |                          Selbyana |         5: 389(-390) |             1981 |                   |                     |        12 |     Orchidaceae |                    Does presence of multiplication sign and whitespace between genus and species name and hyphen in species epithet prevent clustering |
            | 17094880-1 | Dryopteridaceae |  Dryopteris | spec. |                 |                              Schott |                 Dryopteris filix-mas |                Gen. Fil. [Schott] |               ad t 9 |             1834 |                   |                     |        10 | Dryopteridaceae |                                                                                                    Does hybrid epithet cluster with non-hybrid epithet |
            |   993921-1 |     Leguminosae |    Baptisia | spec. |                 |  Kosnik, Diggs, Redshaw & Lipscomb  |                Baptisia × variicolor |                              Sida |           17(2): 498 |             1996 |                   |                     |        11 |     Leguminosae |                                                  Does presence of multiplication sign and whitespace between genus and species name prevent clustering |
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
            | 17291860-1 | Dryopteridaceae |  Dryopteris | spec. |                 |                               C Chr |     Dryopteris filix mas x spinulosa |                                   |                      |                  |                   |                     |         3 | Dryopteridaceae |                                                                                                    Does hybrid epithet cluster with non-hybrid epithet |
            | 17542760-1 | Dryopteridaceae | Dryostichum | spec. |                 |                          W.H.Wagner |              × Dryostichum singulare |                    Canad. J. Bot. |    70(2): 248 (1992) |                0 |                   |                     |         8 | Dryopteridaceae |                                                          Does presence of multiplication sign and whitespace at start of genus name prevent clustering |
            | 17270050-1 | Dryopteridaceae | Dryopteris  | spec. |                 |                        Schott       |                 Dryopteris filix-mas |                                   |                      |                  |                   |                     |         1 | Dryopteridaceae |                                                                                                    Does hybrid epithet cluster with non-hybrid epithet |
            | 17325300-1 | Dryopteridaceae | Dryopteris  | spec. |                 |                        Schott       |                 Dryopteris filix-mas |                                   |                      |                  |                   |                     |         1 | Dryopteridaceae |                                                                                                    Does hybrid epithet cluster with non-hybrid epithet |
            | 17270100-1 | Dryopteridaceae | Dryopteris  | spec. |                 |                                     |     Dryopteris filix mas x spinulosa |                                   |                      |                  |                   |                     |         1 | Dryopteridaceae |                                                                                                    Does hybrid epithet cluster with non-hybrid epithet |

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
                    <constructor-arg value="REPLACE_WITH_TMPDIR/input.txt" />
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
                <util:list id="reporters">
                    <bean class="org.kew.shs.dedupl.reporters.DedupReporter"
                        p:name="dedupReporter"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/output.txt" />
                            </bean>
                        </property>
                    </bean>
                </util:list>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="family" />
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="genus"/>
                        <property name="matcher" ref="exactMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="fakeHybridSignCleaner"/>
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="useInSelect" value="true"/>
                        <property name="indexInitial" value="true"/>
                        <property name="indexLength" value="true"/>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="basionym_author"/>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="publishing_author"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="authorCommonTokensMatcher"/>
                        <property name="blanksMatch" value="true"/>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="full_name_without_family_and_authors"/>
                        <property name="matcher" ref="exactMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="fakeHybridSignCleaner"/>
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                        <property name="useInSelect" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="publication"/>
                        <property name="matcher" ref="capitalLettersMatcher"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphaNumericsTransformer"/>
                            </util:list>
                        </property>
                        <property name="blanksMatch" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="collation"/>
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="collationCleaner"/>
                            </util:list>
                        </property>
                        <property name="matcher" ref="numberMatcher"/>
                        <property name="addOriginalSourceValue" value="true"/>
                        <property name="addTransformedSourceValue" value="true"/>
                        <property name="blanksMatch" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="publication_year"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="blanksMatch" value="true" />
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="collationCleaner"/>
                            </util:list>
                        </property>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="reference_remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="remarks"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="std_score"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property">
                        <property name="sourceColumnName" value="dit_family"/>
                        <property name="matcher" ref="alwaysMatchingMatcher"/>
                        <property name="useInSelect" value="false"/>
                        <property name="addOriginalSourceValue" value="true"/>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="inputfile"
                    p:sourceFileDelimiter="&#09;"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:scoreFieldName="std_score"
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the deduplication-specific bit -->
                <import resource="classpath*:application-context-dedup.xml"/>
            </beans>
            """
        When this species config is run through the Dedupl App
        Then a file should have been created in the same folder with the following species data:
            |         id |          family |  genus      | genus_transf |                 publishing_author |      publishing_author_transf |      full_name_without_family_and_authors | full_name_without_family_and_authors_transf |             publication_transf |            collation | collation_transf | publication_year | reference_remarks |             remarks | std_score |      dit_family | cluster_size |    from_id |                           ids_in_cluster |
            |   307089-2 |        Fabaceae |    Baptisia |     Baptisia | Kosnik, Diggs, Redshaw & Lipscomb | Kosnik Diggs Redshaw Lipscomb |                     Baptisia × variicolor |                  Baptisia variicolor        |                           Sida |              17: 498 |           17 498 |             1996 |                   |                     |        13 |     Leguminosae |            2 |   307089-2 |                     307089-2 \| 993921-1 |
            |   300547-2 |     Orchidaceae |    Barkeria |     Barkeria |                       Soto Arenas |                   Soto Arenas |              Barkeria fritz-halbingeriana |         Barkeria fritz halbingeriana        |           Orquidea Mexico City | 13: 245 (-246; fig.) |   13 245 246 fig |             1993 |                   |                     |        13 |     Orchidaceae |            2 |   300547-2 |                     300547-2 \| 312304-2 |
            | 30345915-2 | Dryopteridaceae |  Dryopteris |   Dryopteris |                            Schott |                        Schott |                      Dryopteris filix-mas |                 Dryopteris filix mas        |                 Gen Fil Schott |                   67 |               67 |             1834 |                   |                     |        13 | Dryopteridaceae |            3 | 30345915-2 |   30345915-2 \| 17270050-1 \| 17325300-1 |
            |   300068-2 | Dryopteridaceae | Dryostichum |  Dryostichum |                        W.H.Wagner |                    W H Wagner |                   × Dryostichum singulare |                Dryostichum singulare        |                    Canad J Bot |      70: 248, figs   |      70 248 figs |             1992 |                   |                     |        12 | Dryopteridaceae |            2 |   300068-2 |                   300068-2 \| 17542760-1 |
            |   319079-2 |     Begoniaceae |     Begonia |      Begonia |                                L. |                             L |                           Begonia obliqua |                      Begonia obliqua        |                          Sp Pl |              2: 1056 |           2 1056 |             1753 |                   | [Gandhi 1 Dec 1999] |        12 |     Begoniaceae |            4 |   319079-2 | 319079-2 \| 1001-2 \| 105293-1 \| 1002-1 |
            |   289660-2 |     Orchidaceae |     Dracula |      Dracula |                              Luer |                          Luer |                Dracula × radio-syndactyla |             Dracula radio syndactyla        |                       Selbyana |         5: 389(-390) |        5 389 390 |             1981 |                   |                     |        12 |     Orchidaceae |            2 |   289660-2 |                     289660-2 \| 897642-1 |
            | 17094880-1 | Dryopteridaceae |  Dryopteris |   Dryopteris |                            Schott |                        Schott |                      Dryopteris filix-mas |                 Dryopteris filix mas        |                 Gen Fil Schott |               ad t 9 |           ad t 9 |             1834 |                   |                     |        10 | Dryopteridaceae |            1 | 17094880-1 |                               17094880-1 |
            |    30810-2 |     Begoniaceae |     Begonia |      Begonia |                      Sessé & Moc. |                     Sesse Moc |                           Begonia peltata |                      Begonia peltata        |                  Fl Mexic ed 2 |                  219 |              219 |             1894 |                   |                     |        11 |     Begoniaceae |            4 |    30810-2 |  30810-2 \| 1003-2 \| 105394-1 \| 1004-1 |
            |    30866-2 |     Begoniaceae |     Begonia |      Begonia |                      Sessé & Moc. |                     Sesse Moc |                            Begonia repens |                       Begonia repens        |                  Fl Mexic ed 2 |                  219 |              219 |             1894 |                   |                     |        11 |     Begoniaceae |            4 |    30866-2 |  30866-2 \| 1005-2 \| 105568-1 \| 1006-1 |
            |    31516-2 |   Berberidaceae |    Berberis |     Berberis |                      Sessé & Moc. |                     Sesse Moc |                          Berberis pinnata |                     Berberis pinnata        |                  Fl Mexic ed 2 |                   84 |               84 |             1894 |                   |                     |        11 |   Berberidaceae |            4 |    31516-2 |  31516-2 \| 1007-2 \| 106995-1 \| 1008-1 |
            |   139242-2 |       Ericaceae |   Leucothoë |    Leucothoe |                           Sleumer |                       Sleumer |                      Leucothoë columbiana |                 Leucothoe columbiana        | Notizbl Bot Gart Berlin Dahlem |              12: 479 |           12 479 |             1935 |                   |                     |        11 |       Ericaceae |            2 |   139242-2 |                     139242-2 \| 331017-1 |
            | 17291860-1 | Dryopteridaceae |  Dryopteris |   Dryopteris |                             C Chr |                         C Chr |          Dryopteris filix mas x spinulosa |     Dryopteris filix mas x spinulosa        |                                |                      |                  |                  |                   |                     |         3 | Dryopteridaceae |            2 | 17291860-1 |                 17291860-1 \| 17270100-1 |

