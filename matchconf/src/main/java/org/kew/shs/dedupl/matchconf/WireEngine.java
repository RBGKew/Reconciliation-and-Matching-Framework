package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;

public class WireEngine {

    Wire wire;

    public WireEngine(Wire wire) {
        this.wire = wire;
    }

    public ArrayList<String> toXML(int indentLevel) {
        int shiftWidth = 4;
        String shift = String.format("%" + shiftWidth + "s", " ");
        String indent = "";
        for (int i=0;i<indentLevel;i++) {
            indent += shift;
        }
        ArrayList<String> outXML = new ArrayList<String>();
        outXML.add(String.format("%s<bean class=\"org.kew.shs.dedupl.configuration.Property\"", indent));
        outXML.add(String.format("%s%sp:name=\"%s\"", indent, shift, this.wire.getName()));
        outXML.add(String.format("%s%sp:useInSelect=\"%s\"", indent, shift, this.wire.getUseInSelect()));
        outXML.add(String.format("%s%sp:useInNegativeSelect=\"%s\"", indent, shift, this.wire.getUseInNegativeSelect()));
        outXML.add(String.format("%s%sp:indexLength=\"%s\"", indent, shift, this.wire.getIndexLength()));
        outXML.add(String.format("%s%sp:blanksMatch=\"%s\"", indent, shift, this.wire.getBlanksMatch()));
        outXML.add(String.format("%s%sp:indexOriginal=\"%s\"", indent, shift, this.wire.getIndexOriginal()));
        outXML.add(String.format("%s%sp:indexInitial=\"%s\"", indent, shift, this.wire.getIndexInitial()));
        outXML.add(String.format("%s%sp:useWildcard=\"%s\"", indent, shift, this.wire.getUseWildcard()));
        outXML.add(String.format("%s%sp:matcher-ref=\"%s\">", indent, shift, this.wire.getMatcher().getName()));

        if (this.wire.getSourceTransformers().size() > 0) {
            outXML.add(String.format("%s%s<property name=\"sourceTransformers\">", indent, shift));
            outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
            for (Bot transformer:this.wire.getSourceTransformers()) {
                outXML.add(String.format("%s%s%s%s<ref bean=\"%s\"/>", indent, shift,shift,  shift, transformer.getName()));
            }
            outXML.add(String.format("%s%s%s</util:list>", indent, shift, shift));
            outXML.add(String.format("%s%s</property>", indent, shift));
        }
        if (this.wire.getLookupTransformers().size() > 0) {
            outXML.add(String.format("%s%s<property name=\"lookupTransformers\">", indent, shift));
            outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
            for (Bot transformer:this.wire.getLookupTransformers()) {
                outXML.add(String.format("%s%s%s%s<ref bean=\"%s\"/>", indent, shift,shift,  shift, transformer.getName()));
            }
            outXML.add(String.format("%s%s%s</util:list>", indent, shift, shift));
            outXML.add(String.format("%s%s</property>", indent, shift));
        }
        outXML.add(String.format("%s</bean>", indent));


        return outXML;
    }

    public ArrayList<String> toXML() {
        return toXML(0);
    }

}
