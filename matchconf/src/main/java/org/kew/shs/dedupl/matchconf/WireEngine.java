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
		outXML.add(String.format("%s%sp:matcher-ref=\"%s\"", indent, shift, this.wire.getMatcher().getName()));
		for (Bot transformer:this.wire.getSourceTransformers()) {
			outXML.add(String.format("%s%sp:transformer-ref=\"%s\"", indent, shift, transformer.getName()));
		}
		outXML.add(String.format("%s%sp:useInSelect=\"%s\"", indent, shift, this.wire.getUseInSelect()));
		outXML.add(String.format("%s%sp:useInNegativeSelect=\"%s\"", indent, shift, this.wire.getUseInNegativeSelect()));
		outXML.add(String.format("%s%sp:indexLength=\"%s\"", indent, shift, this.wire.getIndexLength()));
		outXML.add(String.format("%s%sp:blanksMatch=\"%s\"", indent, shift, this.wire.getBlanksMatch()));
		outXML.add(String.format("%s%sp:indexOriginal=\"%s\"", indent, shift, this.wire.getIndexOriginal()));
		outXML.add(String.format("%s%sp:indexInitial=\"%s\"", indent, shift, this.wire.getIndexInitial()));
		outXML.add(String.format("%s%sp:useWildcard=\"%s\"/>", indent, shift, this.wire.getUseWildcard()));
		return outXML;
	}
	
	public ArrayList<String> toXML() {
		return toXML(0);
	}

}
