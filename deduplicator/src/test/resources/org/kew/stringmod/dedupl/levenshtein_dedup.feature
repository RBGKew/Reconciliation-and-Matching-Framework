Feature: Deduplication with Levenshtein
    As a DataImprover
    In order to use the LevenshteinMatcher
    It has to work

    Scenario: Simple scenario without false positives
        Given Alecs has created an input file
            | id | name | anotherName |
            | 1  | Hinz | exactly     |
            | 2  | Kunz | exactly     |
        And Alecs has set up a configuration file
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
                    <constructor-arg value="REPLACE_WITH_TMPDIR/source.txt" />
                </bean>
                <bean id="exactMatcher" class="org.kew.stringmod.dedupl.matchers.ExactMatcher" />
                <bean id="levenshteinMatcher" class="org.kew.stringmod.dedupl.matchers.LevenshteinMatcher"
                    p:maxDistance="3" />
                <util:list id="reporters">
                    <bean class="org.kew.stringmod.dedupl.reporters.DedupReporter"
                        p:name="dedupReporter"
                        p:configName="thisConfig"
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
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:sourceColumnName="name"
                        p:matcher-ref="levenshteinMatcher"
                        p:addOriginalSourceValue="true" />
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:sourceColumnName="anotherName"
                        p:matcher-ref="exactMatcher"
                        p:useInSelect="true" />
                </util:list>
                <bean id="config" class="org.kew.stringmod.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="inputfile"
                    p:sourceFileDelimiter="&#09;"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:scoreFieldName="id"
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the deduplication-specific bit -->
                <import resource="classpath*:application-context-dedup.xml"/>
            </beans>
            """
        When this config is run through the deduplicator
        Then a file should have been created in the same folder with the following data:
            | id | name | cluster_size | from_id | ids_in_cluster |
            | 2  | Kunz |            2 |       1 |         2 \| 1 |

    Scenario: Scenario with false positives
        Given Alecs has created an input file
            | id | name | anotherName |
            | 1  | Hinz | exactly     |
            | 2  | Kunz | exactly     |
        And he has access to a tab-separated dictionary
            | Hinz | Kunz |
            | Kunz | Hinz |
        And Alecs has set up a configuration file, this time with false positives in a dictionary
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
                    <constructor-arg value="REPLACE_WITH_TMPDIR/source.txt" />
                </bean>
                <bean id="falsePositivesDict" class="org.kew.stringmod.utils.Dictionary"
                     p:fileDelimiter="&#09;"
                     p:filePath="REPLACE_WITH_TMPDIR/funkyDict.txt" />
                <bean id="exactMatcher" class="org.kew.stringmod.dedupl.matchers.ExactMatcher" />
                <bean id="levenshteinMatcher" class="org.kew.stringmod.dedupl.matchers.LevenshteinMatcher"
                    p:maxDistance="3"
                    p:dict-ref="falsePositivesDict" />
                <util:list id="reporters">
                    <bean class="org.kew.stringmod.dedupl.reporters.DedupReporter"
                        p:name="dedupReporter"
                        p:configName="thisConfig"
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
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:sourceColumnName="name"
                        p:matcher-ref="levenshteinMatcher"
                        p:addOriginalSourceValue="true" />
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:sourceColumnName="anotherName"
                        p:matcher-ref="exactMatcher"
                        p:useInSelect="true" />
                </util:list>
                <bean id="config" class="org.kew.stringmod.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="inputfile"
                    p:sourceFileDelimiter="&#09;"
                    p:properties-ref="columnProperties"
                    p:loadReportFrequency="5000"
                    p:writeComparisonReport="true"
                    p:scoreFieldName="id"
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the deduplication-specific bit -->
                <import resource="classpath*:application-context-dedup.xml"/>
            </beans>
            """
        When this config is run through the deduplicator
        Then a file should have been created in the same folder with the following data:
            | id | name | cluster_size | from_id | ids_in_cluster |
            | 1  | Hinz | 1            | 1       | 1              |
            | 2  | Kunz | 1            | 2       | 2              |

