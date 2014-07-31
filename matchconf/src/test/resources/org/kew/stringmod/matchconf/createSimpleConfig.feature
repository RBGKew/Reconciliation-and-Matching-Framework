Feature: Create a simple configuration
    As a developer
    In order to assure my program works roughly
    I want to have a very high-level test in place for a simple standard configuration

    Scenario: DedupConf, one column, one matcher, two transformers, rest default values
        Given Alecs has a file containing data in two columns, ("id_col", "data_col") in a directory "some_path"
        And he has created a new configuration:
            | name          | workDirPath | maxSearchResults | recordFilter                          | nextConfig |
            | simple-config | some_path   | 100              | funny(recordCheck == javaScriptMagic) | someName   |
        And he has added a dictionary "funkyDict" with the filepath field "/some_other_path/dict.txt"
        And he has added the following queryTransformers
            | name               | packageName                     | className                     | params         |
            | funkyTransformer   | org.kew.stringmod.lib.transformers | DictionaryTransformer         | dict=funkyDict |
            | anotherTransformer | org.kew.stringmod.lib.transformers | SafeStripNonAlphasTransformer |                |
            | a2BTransformer     | org.kew.stringmod.lib.transformers | A2BTransformer                | a="a", b=" "   |
        And he has added a matcher for the second column:
            | name         | packageName                 | className    | params            |
            | matchExactly | org.kew.stringmod.dedupl.matchers | ExactMatcher | blanksMatch=false |
        And he has wired them together at the second column
        And he has added the following reporters:
            | name              | fileName             | packageName                  | className              | params |
            | standardReporter  | output.tsv           | org.kew.stringmod.dedupl.reporters | DedupReporter          |        |
            | multilineReporter | output_multiline.tsv | org.kew.stringmod.dedupl.reporters | DedupReporterMultiline |        |
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
                    <constructor-arg value="/tmp/matchconf/lucene_directory/"/>
                </bean>
                <bean id="queryfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/query.tsv" />
                </bean>
                <bean id="funkyDict" class="org.kew.stringmod.utils.Dictionary"
                    p:fileDelimiter="&#09;"
                    p:filePath="/some_other_path/dict.txt" />
                <bean id="matchExactly" class="org.kew.stringmod.dedupl.matchers.ExactMatcher"
                    p:blanksMatch="false"/>
                <bean id="a2BTransformer" class="org.kew.stringmod.lib.transformers.A2BTransformer"
                    p:a="a"
                    p:b=" "/>
                <bean id="anotherTransformer" class="org.kew.stringmod.lib.transformers.SafeStripNonAlphasTransformer" />
                <bean id="funkyTransformer" class="org.kew.stringmod.lib.transformers.DictionaryTransformer"
                    p:dict-ref="funkyDict"/>
                <util:list id="reporters">
                    <bean class="org.kew.stringmod.dedupl.reporters.DedupReporter"
                        p:name="standardReporter"
                        p:configName="simple-config"
                        p:nameSpacePrefix="simple-config_"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/simple-config_output.tsv" />
                            </bean>
                        </property>
                    </bean>
                    <bean class="org.kew.stringmod.dedupl.reporters.DedupReporterMultiline"
                        p:name="multilineReporter"
                        p:configName="simple-config"
                        p:nameSpacePrefix="simple-config_"
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
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:queryColumnName="data_col"
                        p:matcher-ref="matchExactly">
                        <property name="queryTransformers">
                            <util:list id="1">
                                <ref bean="funkyTransformer"/>
                                <ref bean="anotherTransformer"/>
                                <ref bean="a2BTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.stringmod.dedupl.configuration.DeduplicationConfiguration"
                    p:queryFile-ref="queryfile"
                    p:queryFileEncoding="UTF-8"
                    p:queryFileDelimiter="&#09;"
                    p:properties-ref="columnProperties"
                    p:sortFieldName="id"
                    p:loadReportFrequency="50000"
                    p:assessReportFrequency="100"
                    p:maxSearchResults="100"
                    p:recordFilter="funny(recordCheck == javaScriptMagic)"
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the deduplication-specific bit -->
                <import resource="classpath*:application-context-dedup.xml"/>
            </beans>
            """

    Scenario: MatchConf, one column, one matcher, two transformers, rest default values
        Given Alecs has a query-file containing data in three columns, ("id", "data_col1", "otherCol") in a directory "some_path"
        And Alecs has an authority-file containing data in three columns ("id", "data_col1", "other_col") in the same directory
        And he has created a new match configuration:
            | name                | workDirPath |
            | simple-match-config | some_path   |
        And he has added the following query- and authorityTransformers
            | name               | packageName                     | className                     | params |
            | 02BlankTransformer | org.kew.stringmod.lib.transformers | ZeroToBlankTransformer        |        |
            | anotherTransformer | org.kew.stringmod.lib.transformers | SafeStripNonAlphasTransformer |        |
        And he has added two matchers:
            | name           | packageName                 | className        | params            |
            | matchExactly   | org.kew.stringmod.dedupl.matchers | ExactMatcher     | blanksMatch=false |
            | matchIntegers  | org.kew.stringmod.dedupl.matchers | IntegerMatcher   | blanksMatch=true  |
        And he has wired them together in the following way:
            | queryColumnName  | authorityColumnName | queryTransformers                      | authorityTransformers                  | matcher        | useInSelect |
            | data_col1        | data_col1           | anotherTransformer, 02BlankTransformer | 02BlankTransformer, anotherTransformer | matchExactly   | true        |
            | otherCol         | other_col           | 02BlankTransformer                     | anotherTransformer                     | matchIntegers  | false       |
        And he has added the following match-reporters:
            | name              | fileName             | packageName                  | className              | params |
            | standardReporter  | output.tsv           | org.kew.stringmod.dedupl.reporters | MatchReporter          |        |
            | multilineReporter | output_multiline.tsv | org.kew.stringmod.dedupl.reporters | MatchReporterMultiline |        |
        When he asks to write the match-configuration out to the filesystem
        Then the following match-config will be written to "some_path/config_simple-match-config.xml":
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
                    <constructor-arg value="/tmp/matchconf/lucene_directory/"/>
                </bean>
                <bean id="queryfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/query.tsv" />
                </bean>
                <bean id="authorityfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/authority.tsv" />
                </bean>
                <bean id="matchExactly" class="org.kew.stringmod.dedupl.matchers.ExactMatcher"
                    p:blanksMatch="false"/>
                <bean id="matchIntegers" class="org.kew.stringmod.dedupl.matchers.IntegerMatcher"
                    p:blanksMatch="true"/>
                <bean id="02BlankTransformer" class="org.kew.stringmod.lib.transformers.ZeroToBlankTransformer" />
                <bean id="anotherTransformer" class="org.kew.stringmod.lib.transformers.SafeStripNonAlphasTransformer" />
                <util:list id="reporters">
                    <bean class="org.kew.stringmod.dedupl.reporters.MatchReporter"
                        p:name="standardReporter"
                        p:configName="simple-match-config"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/simple-match-config_output.tsv" />
                            </bean>
                        </property>
                    </bean>
                    <bean class="org.kew.stringmod.dedupl.reporters.MatchReporterMultiline"
                        p:name="multilineReporter"
                        p:configName="simple-match-config"
                        p:delimiter="&#09;"
                        p:idDelimiter="|">
                        <property name="file">
                            <bean class="java.io.File">
                                <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/simple-match-config_output_multiline.tsv" />
                            </bean>
                        </property>
                    </bean>
                </util:list>
                <util:list id="columnProperties">
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:queryColumnName="data_col1"
                        p:authorityColumnName="data_col1"
                        p:useInSelect="true"
                        p:matcher-ref="matchExactly">
                        <property name="queryTransformers">
                            <util:list id="1">
                                <ref bean="02BlankTransformer"/>
                                <ref bean="anotherTransformer"/>
                            </util:list>
                        </property>
                        <property name="authorityTransformers">
                            <util:list id="1">
                                <ref bean="anotherTransformer"/>
                                <ref bean="02BlankTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                    <bean class="org.kew.stringmod.dedupl.configuration.Property"
                        p:queryColumnName="otherCol"
                        p:authorityColumnName="other_col"
                        p:matcher-ref="matchIntegers">
                        <property name="queryTransformers">
                            <util:list id="1">
                                <ref bean="02BlankTransformer"/>
                            </util:list>
                        </property>
                        <property name="authorityTransformers">
                            <util:list id="1">
                                <ref bean="anotherTransformer"/>
                            </util:list>
                        </property>
                    </bean>
                </util:list>
                <bean id="config" class="org.kew.stringmod.dedupl.configuration.MatchConfiguration"
                    p:queryFile-ref="queryfile"
                    p:queryFileEncoding="UTF-8"
                    p:queryFileDelimiter="&#09;"
                    p:authorityFile-ref="authorityfile"
                    p:authorityFileEncoding="UTF-8"
                    p:authorityFileDelimiter="&#09;"
                    p:properties-ref="columnProperties"
                    p:sortFieldName="id"
                    p:loadReportFrequency="50000"
                    p:assessReportFrequency="100"
                    p:maxSearchResults="10000"
                    p:recordFilter=""
                    p:reporters-ref="reporters"/>
                <!-- import the generic application-context (equal for dedup/match configurations) -->
                <import resource="classpath*:application-context.xml"/>
                <!-- add the matching-specific bit -->
                <import resource="classpath*:application-context-match.xml"/>
            </beans>
            """
