Feature: run a simple configuration
    Scenario: run the configuration as set up by createSimpleConfig step
        Given Alecs has set up a simple Configuration resulting in the following config "config_simple-config-to-run.xml" written to "REPLACE_WITH_TMPDIR/some_path":
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
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/source.tsv" />
                </bean>
                <bean id="matchExactly" class="org.kew.shs.dedupl.matchers.ExactMatcher"
                    p:blanksMatch="false"/>
                <bean id="02BlankTransformer" class="org.kew.shs.dedupl.transformers.ZeroToBlankTransformer" />
                <bean id="anotherTransformer" class="org.kew.shs.dedupl.transformers.SafeStripNonAlphasTransformer" />
                <util:list id="reporters">
                    <bean class="org.kew.shs.dedupl.reporters.DedupReporter"
                        p:name="outputReporter"
                        p:configName="simple-config-to-run"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/output.tsv" />
                            </bean>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.reporters.DedupReporterMultiline"
                        p:name="outputReporterMultiline"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/output_multiline.tsv" />
                            </bean>
                        </property>
                    </bean>
                </util:list>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="data_col"
                        p:useInSelect="true"
                        p:addOriginalSourceValue="true"
                        p:matcher-ref="matchExactly">
                        <property name="sourceTransformers">
                            <util:list id="1">
                                <ref bean="02BlankTransformer"/>
                                <ref bean="anotherTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.shs.dedupl.configuration.DeduplicationConfiguration"
                    p:sourceFile-ref="sourcefile"
                    p:properties-ref="columnProperties"
                    p:scoreFieldName="id"
                    p:sourceFileEncoding="UTF8"
                    p:sourceFileDelimiter="&#09;"
                    p:loadReportFrequency="50000"
                    p:assessReportFrequency="100"
                    p:reporters-ref="reporters"/>

                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the deduplication-specific bit -->
                <import resource="classpath*:application-context-dedup.xml"/>

            </beans>
            """
        And some mysterious data-improver has put a file "source.tsv" in the same directory containing the following data:
            | id      | data_col  | transformer_comments                               | matcher_comments |
            | 1       | 0         | zero should be replaced with blank                 | stays alone      |
            | 2       | some-name | hyphen should be replaced with white space         | 3 cluster items  |
            | 3       |           | blank stays blank                                  | stays alone      |
            | 4       | some name | stays the same                                     | 3 cluster items  |
            | 5       |           | blank stays blank                                  | stays alone      |
            | 6       | sóme namê | diacrits should be replaced with ascii equivalents | 3 cluster items  |
        When asking MatchConf to run this configuration
        Then the deduplication program should run smoothly and produce the following file "output.tsv" in the same directory:
            | id      | data_col  | configLog            | cluster_size                      | from_id                      | ids_in_cluster |
            | 1       | 0         | simple-config-to-run | 1                                 | 1                            | 1              |
            | 6       | sóme namê | simple-config-to-run | 3                                 | 2                            | 6 \| 4 \| 2    |
            | 3       |           | simple-config-to-run | 1                                 | 3                            | 3              |
            | 5       |           | simple-config-to-run | 1                                 | 5                            | 5              |
