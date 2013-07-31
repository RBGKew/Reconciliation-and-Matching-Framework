# This is a very high-level test with real domain-specific data.
# It should maybe not live within the application?
Feature: Match WCS against Ipni
    As a DataImprover
    In order to match WCS against Ipni
    I want the deduplicator framework to do the work for me, I just have to provide a decent configuration.

    Scenario: Genus level
        Given Eszter has created an source-file to feed the deduplicator framework containing tab-separated WCS data
            | id     | family       | genus       | species_epithet | infraspecies_epithet | rank        | infraspecific_rank | full_name                            | basionym_author | publishing_author          | publication                   | collation         | year |
            | 251171 | Restionaceae | Kulinia     |                 |                      |   Genus     |                    | Kulinia                              |                 | B.G.Briggs & L.A.S.Johnson | Telopea       7: 349          | (1998)            |      |
            | 243223 | Restionaceae | Empodisma   |     gracillimum |                      | Species     |                    | Empodisma gracillimum                | F.Muell.        | L.A.S.Johnson & D.F.Cutler | Kew Bull.      28: 383        | (1973 publ. 1974) |      |
            | 463816 | Restionaceae | Platycaulos |         mahonii |            humbertii |  Subspecies |             subsp. | Platycaulos mahonii subsp. humbertii | Cherm.          | H.P.Linder & C.R.Hardy     | Bothalia        40: 8         | (2010)            |      |
            | 224033 | Restionaceae | Cannomois   |      scirpoides |                minor |     Variety |               var. | Cannomois scirpoides var. minor      |                 | Pillans                    | Trans. Roy. Soc. South Africa | 16: 419 (1928)    |      |
            | 247398 | Restionaceae | Guringalia  |                 |                      |   Genus     |                    | Guringalia                           |                 | B.G.Briggs & L.A.S.Johnson | Telopea       7: 353          | (1998)            |      |
            | 464139 | Restionaceae | Restio      |       saxatilis |                      | Species     |                    | Restio saxatilis                     | Esterh.         | H.P.Linder & C.R.Hardy     | Bothalia       40: 27         | (2010)            |      |

        And she has created a lookup-file to match against
            | id         | family       | genus       | species_epithet | infraspecies_epithet | rank        | infraspecific_rank | full_name                            | basionym_author | publishing_author          | publication                   | collation                 | year | test_comments                                      |
            | 1001674-1  | Restionaceae | Kulinia     |                 |                      | gen.        |                    | Kulinia                              |                 | B.G.Briggs & L.A.S.Johnson | Telopea                       | 7(4): 349 (1998).         | 1998 | Best_match                                         |
            | 715883-1   | Restionaceae | Empodisma   | gracillimum     |                      | spec.       |                    | Empodisma gracillimum                | (F.Muell.)      | L.A.S.Johnson & D.F.Cutler | Kew Bull.                     | 28(3): 383                | 1974 | Best_match                                         |
            | 77108752-1 | Restionaceae | Platycaulos | mahonii         | humbertii            |             | subsp.             | Platycaulos mahonii subsp. humbertii | (Cherm.)        | H.P.Linder & C.R.Hardy     | Bothalia                      | 40(1): 8                  | 2010 | Best_match                                         |
            | 1001674-4  | Restionaceae | Culinia     |                 |                      | gen.        |                    | Culinia                              |                 | B.G.Briggs & L.A.S.Johnson | Telopea                       | 7(4): 349 (1998).         | 1998 | Name LD=1                                          |
            | 715883-4   | Restionaceae | Empodisma   | gracilima       |                      | spec.       |                    | Empodisma gracilima                  | (F.Muell.)      | L.A.S.Johnson & D.F.Cutler | Kew Bull.                     | 28(3): 383                | 1974 | Name LD=2                                          |
            | 715883-5   | Restionaceae | Empodisma   | gracillimum     |                      | spec.       |                    | Empodisma gracillimum                |                 | R.Br.                      | Kew Bull.                     | 28(3): 383                | 1974 | "wrong pub author, no basionym author"             |
            | 77108752-4 | Restionaceae | Platyculos  | mahoni          | humbertii            |             | subsp.             | Platyculos mahoni subsp. humbertii   | (Cherm.)        | H.P.Linder & C.R.Hardy     | Bothalia                      | 40(1): 8                  | 2010 | Name LD=2                                          |
            | 77108752-5 | Restionaceae | Platycaulos | mahonii         | humbertii            |             | subsp.             | Platycaulos mahonii subsp. humbertii |                 | H.P.Linder & C.R.Hardy     | Bothalia South Reg            | 40(2): 9                  | 2009 | "wrong pub, no basionym au, wrong collation"       |
            | 77108795-1 | Restionaceae | Restio      | saxatilis       |                      | spec.       |                    | Restio saxatilis                     | (Esterh.)       | H.P.Linder & C.R.Hardy     | Bothalia                      | 40(1): 27                 | 2010 | Best_match                                         |
            | 1001676-1  | Restionaceae | Guringalia  | gen.            |                      |             |                    | Guringalia                           |                 | B.G.Briggs & L.A.S.Johnson | Telopea                       | 7(4): 353 (1998).         | 1998 | Best_match                                         |
            | 1001677-1  | Restionaceae | Guringalia  | dimorpha        |                      | spec.       |                    | Guringalia dimorpha                  | (R.Br.)         | B.G.Briggs & L.A.S.Johnson | Telopea                       | 7(4): 353 (1998).         | 1998 | Wrong rank shouldn't match                         |
            | 77111821-1 | Restionaceae | Cannomois   | scirpoides      | primosii             |             | var.               | Cannomois scirpoides var. primosii   |                 | Pillans                    | Trans. Roy. Soc. South Africa | 16: 419                   | 1928 | Wrong infraname shouldn't match                    |
            | 77111821-4 | Restionaceae | Cannomois   | scirpoides      | minor                |             | var.               | Cannomois scirpoides var. minor      |                 | Pillans                    | Trans. Roy. Soc. South Africa | 16: 419                   | 1928 | Best_match                                         |
            | 77111821-5 | Restionaceae | Cannomois   | scirpoides      | minor                |             | var.               | Cannomois scirpoides var. minor      |                 | Pillans                    | Roy. South Africa             | 16: 419                   | 1928 | Match on collation but less good than 77111821-5   |
            | 77111821-5 | Restionaceae | Cannomois   | scirpoides      | minor                |             | var.               | Cannomois scirpoides var. minor      |                 | Pillans                    | South Africa                  | 16: 419                   | 1928 | No match on publication (0.49)                     |
            | 77108795-4 | Restionaceae | Restio      | saxatilis       |                      | spec.       |                    | Restio saxatilis                     | (Esterh.)       | H.P.Linder & C.R.Hardy     | Bothalia                      | 40(1): 27 fig. 6-7 (2010) |      | No match on collation (0.49)                       |


        And Alecs has set up a match configuration file according to her specs:
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
                <bean id="sourcefile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/source.txt" />
                </bean>
                <bean id="lookupfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/lookup.txt" />
                </bean>
                <bean id="alwaysMatchingMatcher" class="org.kew.shs.dedupl.matchers.AlwaysMatchingMatcher" />
                <bean id="exactMatcher" class="org.kew.shs.dedupl.matchers.ExactMatcher" />
                <bean id="capitalLettersMatcher" class="org.kew.shs.dedupl.matchers.CapitalLettersMatcher"
                    p:minRatio="1"/>
                <bean id="numberMatcher" class="org.kew.shs.dedupl.matchers.NumberMatcher"
                    p:minRatio="1"/>
                <bean id="safeStripNonAlphasTransformer" class="org.kew.shs.dedupl.transformers.SafeStripNonAlphasTransformer" />
                <bean id="fakeHybridSignCleaner" class="org.kew.shs.dedupl.transformers.FakeHybridSignCleaner" />
                <bean id="safeStripNonAlphaNumericsTransformer" class="org.kew.shs.dedupl.transformers.SafeStripNonAlphaNumericsTransformer" />
                <bean id="rcollationCleaner" class="org.kew.shs.dedupl.transformers.RomanNumeralTransformer " />
                <util:list id="reporters">
                    <bean class="org.kew.shs.dedupl.reporters.MatchReporter"
                        p:name="standardReporter"
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
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="family"
                        p:lookupColumnName="family"
                        p:addTransformedSourceValue="true"
                        p:matcher-ref="alwaysMatchingMatcher">
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="genus"
                        p:lookupColumnName="genus"
                        p:useInSelect="true"
                        p:addOriginalSourceValue="true"
                        p:addTransformedSourceValue="true"
                        p:matcher-ref="exactMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                                <ref bean="fakeHybridSignCleaner"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                                <ref bean="fakeHybridSignCleaner"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="species_epithet"
                        p:lookupColumnName="species_epithet"
                        p:useInSelect="true"
                        p:addOriginalSourceValue="true"
                        p:addTransformedSourceValue="true"
                        p:addOriginalLookupValue="true"
                        p:addTransformedLookupValue="true"
                        p:matcher-ref="exactMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                                <ref bean="fakeHybridSignCleaner"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                                <ref bean="fakeHybridSignCleaner"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="infraspecies_epithet"
                        p:lookupColumnName="infraspecies_epithet"
                        p:useInSelect="true"
                        p:matcher-ref="exactMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                                <ref bean="fakeHybridSignCleaner"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                                <ref bean="fakeHybridSignCleaner"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="basionym_author"
                        p:lookupColumnName="basionym_author"
                        p:blanksMatch="true"
                        p:matcher-ref="exactMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="publishing_author"
                        p:lookupColumnName="publishing_author"
                        p:matcher-ref="exactMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphasTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="publication"
                        p:lookupColumnName="publication"
                        p:matcher-ref="capitalLettersMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphaNumericsTransformer"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphaNumericsTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="collation"
                        p:lookupColumnName="collation"
                        p:blanksMatch="true"
                        p:matcher-ref="numberMatcher">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphaNumericsTransformer"/>
                                <ref bean="rcollationCleaner"/>
                            </util:list>
                        </property>
                        <property name="lookupTransformers">
                            <util:list id="1">
                                <ref bean="safeStripNonAlphaNumericsTransformer"/>
                                <ref bean="rcollationCleaner"/>
                            </util:list>
                        </property>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.MatchConfiguration"
                    p:sourceFile-ref="sourcefile"
                    p:lookupFile-ref="lookupfile"
                    p:properties-ref="columnProperties"
                    p:scoreFieldName="id"
                    p:sourceFileEncoding="UTF8"
                    p:sourceFileDelimiter="&#09;"
                    p:loadReportFrequency="50000"
                    p:assessReportFrequency="100"
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the matching-specific bit -->
                <import resource="classpath*:application-context-match.xml"/>
            </beans>
            """
        When this match config is run through the Match App
        Then a file should have been created in the same folder with the following data:
            | id     | family       | genus_orig  |genus       | species_epithet_orig | species_epithet | lookup_species_epithet_orig | lookup_species_epithet | no_matches | matching_ids |
            | 251171 | Restionaceae | Kulinia     |Kulinia     |                      |                 |                             |                        | 0          |              |
            | 243223 | Restionaceae | Empodisma   |Empodisma   | gracillimum          | gracillimum     |                             |                        | 0          |              |
            | 463816 | Restionaceae | Platycaulos |Platycaulos | mahonii              | mahonii         |                             |                        | 0          |              |
            | 224033 | Restionaceae | Cannomois   |Cannomois   | scirpoides           | scirpoides      |                             |                        | 0          |              |
            | 247398 | Restionaceae | Guringalia  |Guringalia  |                      |                 |                             |                        | 0          |              |
            | 464139 | Restionaceae | Restio      |Restio      | saxatilis            | saxatilis       |                             |                        | 0          |              |
