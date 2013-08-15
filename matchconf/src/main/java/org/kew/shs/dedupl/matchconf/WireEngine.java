package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;

public class WireEngine {

    Wire wire;

    private final String[] defaultFalseList = new String[] {
            "useInSelect", "useInNegativeSelect", "indexLength", "blanksMatch",
            "addOriginalSourceValue", "addOriginalLookupValue",
            "addTransformedSourceValue", "addTransformedLookupValue", "indexInitial",
            "useWildcard"};


    public WireEngine(Wire wire) {
        this.wire = wire;
    }

    public ArrayList<String> toXML(int indentLevel) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        int shiftWidth = 4;
        String shift = String.format("%" + shiftWidth + "s", " ");
        String indent = "";
        for (int i=0;i<indentLevel;i++) {
            indent += shift;
        }
        ArrayList<String> outXML = new ArrayList<String>();
        outXML.add(String.format("%s<bean class=\"org.kew.shs.dedupl.configuration.Property\"", indent));
        outXML.add(String.format("%s%sp:sourceColumnName=\"%s\"", indent, shift, this.wire.getSourceColumnName()));
        if (this.wire.getLookupColumnName().length() > 0) {
	        outXML.add(String.format("%s%sp:lookupColumnName=\"%s\"", indent, shift, this.wire.getLookupColumnName()));
        }

        // all boolean attributes default to false and we only want to write them if they are set to true
        for (String attr:this.defaultFalseList) {
            boolean value = (boolean) this.wire.getClass().getField(attr).get(this.wire);
            if (value) outXML.add(String.format("%s%sp:%s=\"%s\"", indent, shift, attr, value));
        }

        outXML.add(String.format("%s%sp:matcher-ref=\"%s\">", indent, shift, this.wire.getMatcher().getName()));

        if (this.wire.getSourceTransformers().size() > 0) {
            outXML.add(String.format("%s%s<property name=\"sourceTransformers\">", indent, shift));
            outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
            for (WiredTransformer wTrans:this.wire.getSourceTransformers()) {
                outXML.add(String.format("%s%s%s%s<ref bean=\"%s\"/>", indent, shift,shift,  shift, wTrans.getTransformer().getName()));
            }
            outXML.add(String.format("%s%s%s</util:list>", indent, shift, shift));
            outXML.add(String.format("%s%s</property>", indent, shift));
        }
        if (this.wire.getLookupTransformers().size() > 0) {
            outXML.add(String.format("%s%s<property name=\"lookupTransformers\">", indent, shift));
            outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
            for (WiredTransformer wTrans:this.wire.getLookupTransformers()) {
                outXML.add(String.format("%s%s%s%s<ref bean=\"%s\"/>", indent, shift,shift,  shift, wTrans.getTransformer().getName()));
            }
            outXML.add(String.format("%s%s%s</util:list>", indent, shift, shift));
            outXML.add(String.format("%s%s</property>", indent, shift));
        }
        outXML.add(String.format("%s</bean>", indent));


        return outXML;
    }

    public ArrayList<String> toXML() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return toXML(0);
    }

}
