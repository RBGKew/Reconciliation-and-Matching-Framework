package org.kew.shs.dedupl.matchconf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.kew.shs.dedupl.util.CoreApp;

public class ConfigurationEngine {

    Configuration config;
    String luceneDirectory = "/tmp/matchconf/lucene_directory/";


    public ConfigurationEngine(Configuration config) {
        this.config = config;
    }

    public ArrayList<String> toXML() throws Exception {
        int shiftWidth = 4;
        String shift = String.format("%" + shiftWidth + "s", " ");

        ArrayList<String> outXML = new ArrayList<String>();
        String sourceFilePath = new File(new File(this.config.getWorkDirPath()), this.config.getSourceFileName()).getPath();
        String lookupFilePath = new File(new File(this.config.getWorkDirPath()), this.config.getLookupFileName()).getPath();

        outXML.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        outXML.add("<beans xmlns=\"http://www.springframework.org/schema/beans\"");
        outXML.add(String.format("%sxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", shift));
        outXML.add(String.format("%sxmlns:util=\"http://www.springframework.org/schema/util\"", shift));
        outXML.add(String.format("%sxmlns:p=\"http://www.springframework.org/schema/p\"", shift));
        outXML.add(String.format("%sxmlns:c=\"http://www.springframework.org/schema/c\"", shift));
        outXML.add(String.format("%sxsi:schemaLocation=\"", shift));
        outXML.add(String.format("%s%shttp://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd", shift, shift));
        outXML.add(String.format("%s%shttp://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.1.xsd\">", shift, shift));
        outXML.add(String.format("%s<bean id=\"preferencePlaceHolder\" class=\"org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer\">", shift));
        outXML.add(String.format("%s%s<property name=\"locations\">", shift, shift));
        outXML.add(String.format("%s%s%s<list>", shift, shift, shift));
        outXML.add(String.format("%s%s%s</list>", shift, shift, shift));
        outXML.add(String.format("%s%s</property>", shift, shift));
        outXML.add(String.format("%s</bean>", shift));
        outXML.add(String.format("%s<bean id=\"lucene_directory\" class=\"java.lang.String\">", shift, shift));
        outXML.add(String.format("%s%s<constructor-arg value=\"%s\"/>", shift, shift, this.luceneDirectory));
        outXML.add(String.format("%s</bean>", shift));
        outXML.add(String.format("%s<bean id=\"sourcefile\" class=\"java.io.File\">", shift, shift));
        outXML.add(String.format("%s%s<constructor-arg value=\"%s\" />", shift, shift, sourceFilePath));
        outXML.add(String.format("%s</bean>", shift));
        if (this.config.getClassName().equals("MatchConfiguration")) {
            outXML.add(String.format("%s<bean id=\"lookupfile\" class=\"java.io.File\">", shift, shift));
            outXML.add(String.format("%s%s<constructor-arg value=\"%s\" />", shift, shift, lookupFilePath));
            outXML.add(String.format("%s</bean>", shift));
        }

        for (Bot bot:this.config.getMatchers()) {
            outXML.addAll(new BotEngine(bot).toXML("matchers", 1));
        }

        for (Bot bot:this.config.getTransformers()) {
            outXML.addAll(new BotEngine(bot).toXML("transformers", 1));
        }

        if (this.config.getReporters().size() > 0) {
            outXML.add(String.format("%s<util:list id=\"reporters\">", shift));
            for (Reporter reporter:this.config.getReporters()) {
                outXML.addAll(new ReporterEngine(reporter).toXML(2));
            }
            outXML.add(String.format("%s</util:list>", shift));
        }


        outXML.add(String.format("%s<util:list id=\"columnProperties\">", shift));
        for (Wire wire:this.config.getWiring()) {
            outXML.addAll(new WireEngine(wire).toXML(2));
        }

        outXML.add(String.format("%s</util:list>", shift));

        outXML.add(String.format("%s<bean id=\"config\" class=\"%s.%s\"", shift, this.config.getPackageName(), this.config.getClassName()));
        outXML.add(String.format("%s%sp:sourceFile-ref=\"sourcefile\"", shift, shift));
        outXML.add(String.format("%s%sp:sourceFileEncoding=\"%s\"", shift, shift, this.config.getSourceFileEncoding()));
        outXML.add(String.format("%s%sp:sourceFileDelimiter=\"%s\"", shift, shift, this.config.getSourceFileDelimiter()));
        if (this.config.getClassName().equals("MatchConfiguration")) {
            outXML.add(String.format("%s%sp:lookupFile-ref=\"lookupfile\"", shift, shift));
            outXML.add(String.format("%s%sp:lookupFileEncoding=\"%s\"", shift, shift, this.config.getLookupFileEncoding()));
            outXML.add(String.format("%s%sp:lookupFileDelimiter=\"%s\"", shift, shift, this.config.getLookupFileDelimiter()));
        }
        outXML.add(String.format("%s%sp:properties-ref=\"columnProperties\"", shift, shift));
        outXML.add(String.format("%s%sp:scoreFieldName=\"%s\"", shift, shift, this.config.getScoreFieldName()));
        outXML.add(String.format("%s%sp:loadReportFrequency=\"%s\"", shift, shift, this.config.getLoadReportFrequency()));
        outXML.add(String.format("%s%sp:assessReportFrequency=\"%s\"", shift, shift, this.config.getAssessReportFrequency()));
        outXML.add(String.format("%s%sp:maxSearchResults=\"%s\"", shift, shift, this.config.getMaxSearchResults()));
        if (this.config.getReporters().size() > 0) {
            outXML.add(String.format("%s%sp:recordFilter=\"%s\"", shift, shift, this.config.getRecordFilter()));
            outXML.add(String.format("%s%sp:reporters-ref=\"reporters\"/>", shift, shift));
        } else outXML.add(String.format("%s%sp:recordFilter=\"%s\"/>", shift, shift, this.config.getRecordFilter()));

        outXML.add(String.format("%s<!-- import the generic application-context (equal for dedup/match configurations) -->", shift));
        outXML.add(String.format("%s<import resource=\"classpath*:application-context.xml\"/>", shift));
        if (this.config.getClassName().equals("DeduplicationConfiguration")) {
            outXML.add(String.format("%s<!-- add the deduplication-specific bit -->", shift));
            outXML.add(String.format("%s<import resource=\"classpath*:application-context-dedup.xml\"/>", shift));
        } else if (this.config.getClassName().equals("MatchConfiguration")) {
            outXML.add(String.format("%s<!-- add the matching-specific bit -->", shift));
            outXML.add(String.format("%s<import resource=\"classpath*:application-context-match.xml\"/>", shift));
        } else throw new Exception("No or wrong Configuration Class Name; this should not happen, contact the developer.");

        outXML.add("</beans>");

        return outXML;
    }

    public void write_to_filesystem () throws Exception {
        // Perform a few checks:
        // 1. does the working directory exist?
        File workDir = new File(this.config.getWorkDirPath());
        if (!workDir.exists()) {
            throw new FileNotFoundException(String.format("The specified working directory %s does not exist! You need to create it and put the source file in it.", config.getWorkDirPath()));
        }
        // 2a. does the source file exist?
        File sourceFile = new File(workDir, config.getSourceFileName());
        if (!sourceFile.exists()) {
            throw new FileNotFoundException(String.format("There is no file found at the specified location of the source-file %s. Move the source file there with the specified sourceFileName.", sourceFile.toPath()));
        }
        // 2b. if the config is for matching: does the lookup file exist?
        if (this.config.getClassName().equals("MatchConfiguration")) {
            File lookupFile = new File(workDir, config.getLookupFileName());
            if (!lookupFile.exists()) {
                throw new FileNotFoundException(String.format("There is no file found at the specified location of the lookup-file %s. Move the lookup file there with the specified lookupFileName.", lookupFile.toPath()));
            }
        }
        // 3. write out the xml-configuration file
        File configFile = new File(workDir, "config_" + config.getName() + ".xml");
        if (configFile.exists()) {
            // rename existing config file and save new one under the actual name
            configFile.renameTo(new File(configFile.toString() + "_older_than_" + DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss").print(new DateTime())));
            configFile = new File(workDir, "config_" + config.getName() +  ".xml");
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(configFile))) {
            br.write(StringUtils.join(this.toXML(), System.getProperty("line.separator")));
        }
    }

    public Map<String, List<String>> runConfiguration () throws Exception {
        return this.runConfiguration(true);
    }

    @SuppressWarnings("finally")
    public Map<String, List<String>> runConfiguration (boolean writeBefore) throws Exception {
        // TODO: is there an internal messaging system in spring mvc?
        @SuppressWarnings("serial")
        Map<String, List<String>> infoMap = new HashMap<String, List<String>>() {{
            put("messages", new ArrayList<String>());
            put("exception", new ArrayList<String>());
            put("stackTrace", new ArrayList<String>());
        }};
        try {
            if (writeBefore) this.write_to_filesystem();
            infoMap.get("messages").add(String.format("%s: Written config file to %s..", this.config.getName(), this.config.getWorkDirPath()));
            File workDir = new File(this.config.getWorkDirPath());
            assert workDir.exists();
            // assert the luceneDirectory does NOT exist (we want to overwrite the index for now!)
            File luceneDir = new File(this.luceneDirectory);
            if (luceneDir.exists()) FileUtils.deleteDirectory(luceneDir);
            CoreApp.main(new String[] {"-d " + workDir.toString(), "-c config_" + this.config.getName() + ".xml"});
            infoMap.get("messages").add(String.format("%s: Ran without complains.", this.config.getName()));
            // Our super-sophisticated ETL functionality:
            String next = this.config.getNextConfig();
            if (!StringUtils.isBlank(this.config.getNextConfig())) {
                ConfigurationEngine nextEngine = new ConfigurationEngine(Configuration.findConfigurationsByNameEquals(next).getSingleResult());
                Map<String, List<String>> nextInfoMap = nextEngine.runConfiguration();
                infoMap.get("exception").addAll(nextInfoMap.get("exception"));
                infoMap.get("stackTrace").addAll(nextInfoMap.get("stackTrace"));
                infoMap.get("messages").addAll(nextInfoMap.get("messages"));
            }
        } catch (Exception e) {
            infoMap.get("exception").add(e.toString());
            for (StackTraceElement ste:e.getStackTrace()) infoMap.get("stackTrace").add(ste.toString());
        } catch (Error e) {
            infoMap.get("exception").add(e.toString());
            for (StackTraceElement ste:e.getStackTrace()) infoMap.get("stackTrace").add(ste.toString());
        } finally {
            File luceneDir = new File(this.luceneDirectory);
            if (luceneDir.exists()) FileUtils.deleteDirectory(luceneDir);
            return infoMap;
        }
    }

}
