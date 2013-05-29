# This is a very high-level test with real "business" data (have I mentioned I don't like the word business?).
# It should maybe not live within the application?
Feature: Deduplicate Ipni
	As a DataImprover
	In order to deduplicate Ipni
	I want the deduplicator framework to do the work for me, I just have to provide a decent configuration.

	Scenario: Genus level
		Given Rachel has created an input-file to feed the deduplicator framework containing tab-separated Genus data
			| Id         | Family      | Genus           | Authors     | Basionym_author | Publishing_author | Full_name_without_family_and_authors | Publication              | Collation     | Publication_year | Reference_remarks | Remarks | std_score | Test_concern                                                                                      |
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

		And Alecs has set up a configuration file according to her specs:
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
                <bean id="safeStripNonAlphanumericsTransformer"
                    class="org.kew.shs.dedupl.transformers.SafeStripNonAlphanumericsTransformer" />
                <bean id="collationCleaner"
                    class="org.kew.shs.dedupl.transformers.CompositeTransformer">
                    <property name="transformers">
                        <util:list id="1">
                            <bean id="icollationCleaner"
                                class="org.kew.shs.dedupl.transformers.StripNonAlphanumericCharactersTransformer" />
                            <bean id="rcollationCleaner"
                                class="org.kew.shs.dedupl.transformers.RomanNumeralTransformer" />
                        </util:list>
                    </property>
                </bean>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="family"
                        p:columnIndex="1"
                        p:matcher-ref="alwaysMatchingMatcher"
                        p:useInSelect="false"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="genus"
                        p:columnIndex="2"
                        p:matcher-ref="exactMatcher"
                        p:transformer-ref="safeStripNonAlphanumericsTransformer"
                        p:useInSelect="true"
                        p:indexInitial="true"
                        p:indexLength="true"
                        p:useWildcard="true"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="basionymAuthor"
                        p:columnIndex="4"
                        p:matcher-ref="authorCommonTokensMatcher"
                        p:transformer-ref="safeStripNonAlphasTransformer"
                        p:blanksMatch="true"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="author"
                        p:columnIndex="5"
                        p:transformer-ref="safeStripNonAlphasTransformer"
                        p:matcher-ref="authorCommonTokensMatcher"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="publication"
                        p:columnIndex="7"
                        p:matcher-ref="capitalLettersMatcher"
                        p:transformer-ref="safeStripNonAlphanumericsTransformer"
                        p:blanksMatch="true"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="collation"
                        p:columnIndex="8"
                        p:transformer-ref="collationCleaner"
                        p:matcher-ref="numberMatcher"
                        p:indexOriginal="true"
                        p:blanksMatch="true"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="year"
                        p:columnIndex="9"
                        p:matcher-ref="alwaysMatchingMatcher"
                        p:blanksMatch="true"
                        p:transformer-ref="yearCleaner"/>
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="std_score"
                        p:columnIndex="12"
                        p:matcher-ref="alwaysMatchingMatcher"
                        p:useInSelect="false"/>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:inputFile-ref="inputfile"
                    p:inputFileDelimiter="&#09;"
                    p:inputFileIgnoreHeader="true"
                    p:outputFileDelimiter="&#09;"
                    p:outputFileIdDelimiter=","
                    p:outputFile-ref="outputfile"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:reportFile-ref="reportfile"
                    p:writeTopCopyReport="true"
                    p:topCopyFile-ref="topcopyfile"
                    p:scoreFieldName="std_score"/>
            </beans>
            """
		When this is run through the Dedupl App
		Then a file should have been created in the same folder with the following data:
            | Id         | Family      | Genus           | Authors     | Basionym_author | Publishing_author | Full_name_without_family_and_authors | Publication              | Collation     | Publication_year | Reference_remarks | Remarks | std_score | cluster_size | from_id    | ids_in_cluster                  | Test_concern                                                                                      |
            | 30022170-2 | Rapateaceae | Saxo-fridericia | R.H.Schomb. |                 | R.H.Schomb.       | Saxo-fridericia                      | Rapatea                  | 13            | 1845             |                   |         | 9         | 3            | 30022170-2 | 30022170-2 \| 33288-1 \| 9999-1 | Does presence of hyphen in genus name prevent clustering                                          |
            | 1001-1     | Ericaceae   | Leucothoë       | D.Don       |                 | D.Don             | Leucothoë                            | Edinburgh New Philos. J. | 17: 159       | 1834             |                   |         | 9         | 2            | 1001-1     | 1001-1 \| 1001-2                | Does presence of diacritic character at end of name prevent clustering                            | 
            | 2001-1     | Ericaceae   | Lëucothoe       | D.Don       |                 | D.Don             | Lëucothoe                            | Edinburgh New Philos. J. | 17: 158       | 1834             |                   |         | 9         | 2            | 2001-1     | 2001-1 \| 2001-2                | Does presence of diacritic character in middle of name prevent clustering                         |
            | 6001-1     | Ericaceae   | Leucothoe       | D.Dön       |                 | D.Dön             | Leucothoe                            | Edinburgh New Philos. J. | 16: 158       | 1834             |                   |         | 9         | 2            | 6001-1     | 6001-1 \| 6001-2                | Does presence of diacritic character in middle of author prevent clustering                       |
            | 7001-1     | Ericaceae   | Leucothoe       | D.Donë      |                 | D.Donë            | Leucothoe                            | Edinburgh New Philos. J. | 15: 158       | 1834             |                   |         | 9         | 2            | 7001-1     | 7001-1 \| 7001-2                | Does presence of diacritic character at end of author prevent clustering                          |
            | 28674-1    | Orchidaceae | Amphorchis      | Thouars     |                 | Thouars           | Amphorchis                           | Hist. Orchid.            | Tabl. Synopt. | 1822             |                   |         | 8         | 2            | 28674-1    | 28674-1 \| 286751               | Does absence of numerical characters in genus name prevent clustering                             |
            | 3001-1     | Orchidaceae | × Amphorchis    | Thouars     |                 | Thouars           | × Amphorchis                         | Hist. Orchid.            | 211           | 1822             |                   |         | 8         | 2            | 3001-1     | 3001-1 \| 3001-2                | Does presence of multiplication sign and whitespace at start of genus name prevent clustering     |
            | 4001-1     | Orchidaceae | ×Amphorchis     | Thouars     |                 | Thouars           | ×Amphorchis                          | Hist. Orchid.            | 56            | 1822             |                   |         | 8         | 2            | 4001-1     | 4001-1 \| 4001-2                | Does presence of multiplication sign without whitespace at start of genus name prevent clustering |
            | 5001-1     | Orchidaceae | X Amphorchis    | Thouars     |                 | Thouars           | X Amphorchis                         | Hist. Orchid.            | 11            | 1822             |                   |         | 8         | 2            | 5001-1     | 5001-1 \| 5001-2                | Does presence of capital letter X at start of name prevent clustering                             |



#	Scenario: Species level
#		Given Eszter has created a folder "species-deduplication-ipni" containing an input-file to feed the deduplicator framework with:
#			| | | |
#		And Alecs has set up a configuration file in the same folder according to her specs:
#			<xml>..</xml>
#		When this is run through the Dedupl App
#		Then a file should have been created in the same folder with the following data:
#			| | | |
