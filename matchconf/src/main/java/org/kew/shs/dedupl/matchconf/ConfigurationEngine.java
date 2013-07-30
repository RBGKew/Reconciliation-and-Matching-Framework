package org.kew.shs.dedupl.matchconf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.kew.shs.dedupl.util.CoreApp;

public class ConfigurationEngine {
	
	Configuration config;
	
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
		outXML.add(String.format("%s%s<constructor-arg value=\"target/deduplicator\"/>", shift, shift));
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
		if (this.config.getClassName().equals("MatchConfiguration")) {
			outXML.add(String.format("%s%sp:lookupFile-ref=\"lookupfile\"", shift, shift));
		}
		outXML.add(String.format("%s%sp:properties-ref=\"columnProperties\"", shift, shift));
		outXML.add(String.format("%s%sp:scoreFieldName=\"%s\"", shift, shift, this.config.getScoreFieldName()));
		outXML.add(String.format("%s%sp:sourceFileEncoding=\"%s\"", shift, shift, this.config.getSourceFileEncoding()));
		outXML.add(String.format("%s%sp:sourceFileDelimiter=\"%s\"", shift, shift, this.config.getSourceFileDelimiter()));
		outXML.add(String.format("%s%sp:loadReportFrequency=\"%s\"", shift, shift, this.config.getLoadReportFrequency()));
		outXML.add(String.format("%s%sp:assessReportFrequency=\"%s\"", shift, shift, this.config.getAssessReportFrequency()));
		if (this.config.getReporters().size() > 0) outXML.add(String.format("%s%sp:reporters-ref=\"reporters\"/>", shift, shift));

		outXML.add(String.format("%s<!-- import the generic application-context (equal for dedup/match configurations) -->", shift));
		outXML.add(String.format("%s<import resource=\"classpath*:application-context.xml\"/>", shift));
		outXML.add(String.format("%s<!-- add the deduplication-specific bit -->", shift));
		if (this.config.getClassName().equals("DeduplicationConfiguration")) {
			outXML.add(String.format("%s<import resource=\"classpath*:application-context-dedup.xml\"/>", shift));
		} else if (this.config.getClassName().equals("DeduplicationConfiguration")) {
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
			throw new FileNotFoundException("The specified working directory ${config.workDirPath} does not exist! You need to create it and put the source file in it.");
		}
		// 2. does the source file exist?
		File sourceFile = new File(workDir, config.getSourceFileName());
		if (!sourceFile.exists()) {
			throw new FileNotFoundException(String.format("There is no file found at the specified location of the source-file %s. Move the source file there with the specified sourceFileName.", sourceFile.toPath()));
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
	
	public void runConfiguration () throws Exception {
		File workDir = new File(this.config.getWorkDirPath());
		assert workDir.exists();
		CoreApp.main(new String[] {"-d " + workDir.toString(), "-c config_" + this.config.getName() + ".xml"});
	}

}