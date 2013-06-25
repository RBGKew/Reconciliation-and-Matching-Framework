Feature: Create a simple configuration
    As a developer
    In order to assure my program works roughly
    I want to have a very high-level test in place for a simple standard configuration

    Scenario: DedupConf, one column, one matcher, one composite-transformer with two transformers, rest default values
        Given Alecs has a file containing data in two columns, ("id_col", "data_col") in a directory "some_path"
        And he has added a composite transformer for the first column:
            | name         		| packageName                 	  | className  		     | params |
            | compiTransformer  | org.kew.shs.dedupl.transformers | CompositeTransformer |        |
        And this Composite Transformer contains the following transformers
            | name         		 | packageName                 	   | className    		    	   | params |
            | 02BlankTransformer | org.kew.shs.dedupl.transformers | ZeroToBlankTransformer 	   |        |
            | anotherTransformer | org.kew.shs.dedupl.transformers | SafeStripNonAlphasTransformer |        |
        And he has added a matcher for the first column:
            | name         | packageName                 | className    | params            |
            | matchExactly | org.kew.shs.dedupl.matchers | ExactMatcher | blanksMatch=false |
        And he has wired the matcher and the transformer to the column in a new configuration:
        	| name 			| workDirPath |
        	| simple-config | some_path   |
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
                <bean id="inputfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/input.tsv" />
                </bean>
                <bean id="outputfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/output_simple-config.tsv" />
                </bean>
                <bean id="topcopyfile" class="java.io.File">
                    <constructor-arg value="REPLACE_WITH_TMPDIR/some_path/output-multiline_simple-config.tsv" />
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
                        p:columnIndex="1"
                        p:matcher-ref="matchExactly"
                        p:transformer-ref="compiTransformer"
                        p:useInSelect="false"
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
                    p:inputFileIgnoreHeader="false"
                    p:outputFileDelimiter="&#09;"
                    p:outputFileIdDelimiter="|"
                    p:loadReportFrequency="50000"
                    p:assessReportFrequency="100"/>
            </beans>
            """
