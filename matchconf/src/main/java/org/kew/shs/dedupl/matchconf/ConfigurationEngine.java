package org.kew.shs.dedupl.matchconf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.kew.shs.dedupl.util.DeduplApp;

public class ConfigurationEngine {
	
	Configuration config;
	
	public ConfigurationEngine(Configuration config) {
		this.config = config;
	}

	public ArrayList<String> toXML() {
		int shiftWidth = 4;
		String shift = String.format("%" + shiftWidth + "s", " ");
		
		ArrayList<String> outXML = new ArrayList<String>();
        String sourceFilePath = new File(new File(this.config.getWorkDirPath()), this.config.getSourceFileName()).getPath();
        String outputFileName = String.format("output_%s.%s", this.config.getName(), this.config.getOutputFileNameExtension());
        String outputFilePath = new File(new File(this.config.getWorkDirPath()), outputFileName).getPath();
        String outputMultilineFileName = String.format("output-multiline_%s.%s", this.config.getName(), this.config.getOutputFileNameExtension());
        String outputMultilineFilePath = new File(new File(this.config.getWorkDirPath()), outputMultilineFileName).getPath();
		
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
		outXML.add(String.format("%s<bean id=\"inputfile\" class=\"java.io.File\">", shift, shift));
		outXML.add(String.format("%s%s<constructor-arg value=\"%s\" />", shift, shift, sourceFilePath));
		outXML.add(String.format("%s</bean>", shift));
		outXML.add(String.format("%s<bean id=\"outputfile\" class=\"java.io.File\">", shift));
		outXML.add(String.format("%s%s<constructor-arg value=\"%s\" />", shift, shift, outputFilePath));
		outXML.add(String.format("%s</bean>", shift));
	    outXML.add(String.format("%s<bean id=\"topcopyfile\" class=\"java.io.File\">", shift));
	    outXML.add(String.format("%s%s<constructor-arg value=\"%s\" />", shift, shift, outputMultilineFilePath));
		outXML.add(String.format("%s</bean>", shift));
			
		for (Bot bot:this.getMatchers()) {
			outXML.addAll(new BotEngine(bot).toXML("matchers", 1));
		}
		for (Bot bot:this.getTransformers()) {
			outXML.addAll(new BotEngine(bot).toXML("transformers", 1));
		}

		outXML.add(String.format("%s<util:list id=\"columnProperties\">", shift));
		for (Wire wire:this.config.getWiring()) {

			outXML.addAll(new WireEngine(wire).toXML(2));
			
		}
		outXML.add(String.format("%s</util:list>", shift));

		outXML.add(String.format("%s<bean id=\"config\" class=\"%s.%s\"", shift, this.config.getPackageName(), this.config.getClassName()));
		outXML.add(String.format("%s%sp:inputFile-ref=\"inputfile\"", shift, shift));
		outXML.add(String.format("%s%sp:outputFile-ref=\"outputfile\"", shift, shift));
		outXML.add(String.format("%s%sp:topCopyFile-ref=\"topcopyfile\"", shift, shift));
		outXML.add(String.format("%s%sp:properties-ref=\"columnProperties\"", shift, shift));
		outXML.add(String.format("%s%sp:scoreFieldName=\"%s\"", shift, shift, this.config.getScoreFieldName()));
		outXML.add(String.format("%s%sp:inputFileEncoding=\"%s\"", shift, shift, this.config.getSourceFileEncoding()));
		outXML.add(String.format("%s%sp:inputFileDelimiter=\"%s\"", shift, shift, this.config.getSourceFileDelimiter()));
		outXML.add(String.format("%s%sp:outputFileDelimiter=\"%s\"", shift, shift, this.config.getOutputFileDelimiter()));
		outXML.add(String.format("%s%sp:outputFileIdDelimiter=\"%s\"", shift, shift, this.config.getOutputFileIdDelimiter()));
		outXML.add(String.format("%s%sp:loadReportFrequency=\"%s\"", shift, shift, this.config.getLoadReportFrequency()));
		outXML.add(String.format("%s%sp:assessReportFrequency=\"%s\"/>", shift, shift, this.config.getAssessReportFrequency()));
		outXML.add("</beans>");
		
		return outXML;
	}

	public SortedSet<Bot> getMatchers() {
		SortedSet<Bot> matchers = new TreeSet<Bot>();
		for (Wire wire:this.config.getWiring()) {
			matchers.add(wire.getMatcher());
		}
		return matchers;
	}
	
	public SortedSet<Bot> getTransformers() {
		SortedSet<Bot> transformers= new TreeSet<Bot>();
		for (Wire wire:this.config.getWiring()) {
			for (Bot transformer:wire.getSourceTransformers()) {
				transformers.add(transformer);
			}
		}
		return transformers;
	}
	
	public void write_to_filesystem () throws IOException {
		// Perform a few checks:
		// 1. does the working directory exist?
		File workDir = new File(this.config.getWorkDirPath());
		if (!workDir.exists()) {
			throw new FileNotFoundException("The specified working directory ${config.workDirPath} does not exist! You need to create it and put the input file in it.");
		}
		// 2. does the input file exist?
		File sourceFile = new File(workDir, config.getSourceFileName());
		if (!sourceFile.exists()) {
			throw new FileNotFoundException(String.format("There is no file found at the specified location of the input-file %s. Move the source file there with the specified inputFileName.", sourceFile.toPath()));
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
		DeduplApp.main(new String[] {"-d " + workDir.toString(), "-c config_" + this.config.getName() + ".xml"});
	}

}