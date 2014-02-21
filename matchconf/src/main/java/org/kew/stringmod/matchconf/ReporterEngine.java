package org.kew.stringmod.matchconf;

import java.util.ArrayList;


/**
 * Writes out the xml to configure the 'real' deduplication/matching process of this specific
 * {@link Reporter} instance.
 */
public class ReporterEngine {

    Reporter reporter;

    public ReporterEngine(Reporter reporter) {
        this.reporter = reporter;
    }

    public ArrayList<String> toXML(int indentLevel) {
        int shiftWidth = 4;
        String shift = String.format("%" + shiftWidth + "s", " ");
        String indent = "";
        for (int i=0;i<indentLevel;i++) {
            indent += shift;
        }
        ArrayList<String> outXML = new ArrayList<String>();
        outXML.add(String.format("%s<bean class=\"%s.%s\"", indent, this.reporter.getPackageName(), this.reporter.getClassName()));
        outXML.add(String.format("%s%sp:name=\"%s\"", indent, shift, this.reporter.getName()));
        outXML.add(String.format("%s%sp:configName=\"%s\"", indent, shift, this.reporter.getConfig().getName()));
        // nameSpacePrefix: if not overwritten on reporter-level...
        if (!this.reporter.getParams().contains("nameSpacePrefix")) {
            // ..check if it's a dedup config..
            if (this.reporter.getConfig().getClassName().equals("DeduplicationConfiguration")) {
                // and tell it to name-space with the dedup-config's name
                outXML.add(String.format("%s%sp:nameSpacePrefix=\"%s_\"", indent, shift, this.reporter.getConfig().getName()));
            }
        }
        outXML.add(String.format("%s%sp:delimiter=\"%s\"", indent, shift, this.reporter.getDelimiter()));
        outXML.add(String.format("%s%sp:idDelimiter=\"%s\"", indent, shift, this.reporter.getIdDelimiter()));
        if (this.reporter.getParams().length() > 0) {
            for (String param:this.reporter.getParams().split(",")) {
                String key, value;
                String[] paramTuple = param.split("=");
                key = paramTuple[0];
                value = paramTuple[1];
                    outXML.add(String.format("%s%sp:%s=\"%s\"", indent, shift, key, value));
            }
        }
        outXML.set(outXML.size()-1, outXML.get(outXML.size()-1) + ">");
        outXML.add(String.format("%s%s<property name=\"file\">", indent, shift));
        outXML.add(String.format("%s%s%s<bean class=\"java.io.File\">", indent, shift, shift));
        outXML.add(String.format("%s%s%s%s<constructor-arg value=\"%s/%s_%s\" />", indent, shift, shift, shift, this.reporter.getConfig().getWorkDirPath(), this.reporter.getConfig().getName(), this.reporter.getFileName()));
        outXML.add(String.format("%s%s%s</bean>", indent, shift, shift));
        outXML.add(String.format("%s%s</property>", indent, shift));
        outXML.add(String.format("%s</bean>", indent));

        return outXML;
    }

    public ArrayList<String> toXML() {
        return toXML(0);
    }
}
