Feature: Create a simple configuration
    As a developer
    In order to assure my program works roughly
    I want to have a very high-level test in place for a simple standard configuration

    Scenario: DedupConf, one column, one matcher, one composite-transformer with two transformers, rest default values
        Given Alecs has a file containing data in two columns, ("id_col", "data_col") in a directory "some_path"
        And he has created a new configuration:
            | name          | workDirPath |
            | simple-config | some_path   |
        And he has added the following sourceTransformers
            | name               | packageName                     | className                     | params |
            | 02BlankTransformer | org.kew.shs.dedupl.transformers | ZeroToBlankTransformer        |        |
            | anotherTransformer | org.kew.shs.dedupl.transformers | SafeStripNonAlphasTransformer |        |
        And he has added a matcher for the second column:
            | name         | packageName                 | className    | params            |
            | matchExactly | org.kew.shs.dedupl.matchers | ExactMatcher | blanksMatch=false |
        And he has wired them together at the second column
        And he has added the following reporters:
            | name              | fileName             | packageName                  | className              | params |
            | standardReporter  | output.tsv           |org.kew.shs.dedupl.reporters  | DedupReporter          |        |
            | multilineReporter | output_multiline.tsv | org.kew.shs.dedupl.reporters | DedupReporterMultiline |        |
        When he asks to write the configuration out to the filesystem
        Then the following content will be written to "some_path/config_simple-config.xml":
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
                        p:name="standardReporter"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/simple-config_output.tsv" />
                            </bean>
                        </property>
                    </bean>
                    <bean class="org.kew.shs.dedupl.reporters.DedupReporterMultiline"
                        p:name="multilineReporter"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/simple-config_output_multiline.tsv" />
                            </bean>
                        </property>
                    </bean>
                </util:list>
                <util:list id="columnProperties">
                    <bean class="org.kew.shs.dedupl.configuration.Property"
                        p:sourceColumnName="data_col"
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
