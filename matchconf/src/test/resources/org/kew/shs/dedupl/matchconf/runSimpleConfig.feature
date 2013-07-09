Feature: run a simple configuration
    Scenario: run the configuration as set up by createSimpleConfig step
        Given Alecs has set up a simple Configuration resulting in the following config "config_simple-config.xml" written to "REPLACE_WITH_TMPDIR/some_path":
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
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/input.tsv" />
                </bean>
                <bean id="outputfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/output.tsv" />
                </bean>
                <bean id="topcopyfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/output_multiline.tsv" />
                </bean>
                <bean id="matchExactly" class="org.kew.shs.dedupl.matchers.ExactMatcher"
                    p:blanksMatch="false"/>
                <bean id="compiTransformer" class="org.kew.shs.dedupl.transformers.CompositeTransformer">
                    <property name="transformers">
                        <util:list id="1">
                            <bean id="02BlankTransformer" class="org.kew.shs.dedupl.transformers.ZeroToBlankTransformer" />
                            <bean id="anotherTransformer" class="org.kew.shs.dedupl.transformers.SafeStripNonAlphasTransformer" />
                        </util:list>
                    </property>
                </bean>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:name="data_col"
                        p:matcher-ref="matchExactly"
                        p:transformer-ref="compiTransformer"
                        p:useInSelect="true"
                        p:useInNegativeSelect="false"
                        p:indexLength="false"
                        p:blanksMatch="false"
                        p:indexOriginal="false"
                        p:indexInitial="false"
                        p:useWildcard="false"/>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:inputFile-ref="inputfile"
                    p:outputFile-ref="outputfile"
                    p:topCopyFile-ref="topcopyfile"
                    p:properties-ref="columnProperties"
                    p:scoreFieldName="id"
                    p:inputFileEncoding="UTF8"
                    p:inputFileDelimiter="&#09;"
                    p:outputFileDelimiter="&#09;"
                    p:outputFileIdDelimiter="|"
                    p:loadReportFrequency="50000"
                    p:assessReportFrequency="100"/>
            </beans>
            """
        And some mysterious data-improver has put a file "input.tsv" in the same directory containing the following data:
            | id      | data_col  | transformer_comments                               | matcher_comments |
            | 1       | 0         | zero should be replaced with blank                 | stays alone      |
            | 2       | some-name | hyphen should be replaced with white space         | 3 cluster items  |
            | 3       |           | blank stays blank                                  | stays alone      |
            | 4       | some name | stays the same                                     | 3 cluster items  |
            | 5       |           | blank stays blank                                  | stays alone      |
            | 6       | sóme namê | diacrits should be replaced with ascii equivalents | 3 cluster items  |
        When asking MatchConf to run this configuration
        Then the deduplication program should run smoothly and produce the following file "output.tsv" in the same directory:
            | id      | data_col  | cluster_size | from_id | ids_in_cluster |
            | 1       |           | 1            | 1       | 1              |
            | 6       | some name | 3            | 2       | 6 \| 4 \| 2    |
            | 3       |           | 1            | 3       | 3              |
            | 5       |           | 1            | 5       | 5              |
