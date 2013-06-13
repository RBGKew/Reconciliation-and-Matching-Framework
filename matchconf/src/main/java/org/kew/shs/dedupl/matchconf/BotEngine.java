package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;

public class BotEngine {

	Bot bot;
	
	public BotEngine(Bot bot) {
		this.bot = bot;
	}
	
	public ArrayList<String> toXML(String treatAs, int indentLevel) {
		int shiftWidth = 4;
		String shift = String.format("%" + shiftWidth + "s", " ");
		String indent = "";
		for (int i=0;i<indentLevel;i++) {
			indent += shift;
		}
		ArrayList<String> outXML = new ArrayList<String>();
		if (this.bot.getComposedBy().size() > 0) {
			outXML.add(String.format("%s<bean id=\"%s\" class=\"%s.%s\">", indent, this.bot.getName(), this.bot.getPackageName(), this.bot.getClassName()));
		    outXML.add(String.format("%s%s<property name=\"%s\">", indent, shift, treatAs));
		    outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
		    for (Bot bot:this.bot.getComposedBy()) {
				outXML.addAll(new BotEngine(bot).toXML(treatAs, indentLevel+3));
		    }
		    outXML.add(String.format("%s%s%s</util:list>", indent, shift, shift));
		    outXML.add(String.format("%s%s</property>", indent, shift));
			outXML.add(String.format("%s</bean>", indent));
		} else {
			if (this.bot.getParams().length() > 0) {
				outXML.add(String.format("%s<bean id=\"%s\" class=\"%s.%s\"", indent, this.bot.getName(), this.bot.getPackageName(), this.bot.getClassName()));
				for (String param:this.bot.getParams().split(",")) {
					String key, value;
					String[] paramTuple = param.split("=");
					key = paramTuple[0];
					value = paramTuple[1];
					outXML.add(String.format("%s%sp:%s=\"%s\"", indent, shift, key, value));
				}
				outXML.set(outXML.size()-1, outXML.get(outXML.size()-1) + "/>");
			} else {
				outXML.add(String.format("%s<bean id=\"%s\" class=\"%s.%s\" />", indent, this.bot.getName(), this.bot.getPackageName(), this.bot.getClassName()));
			}

		}
		return outXML;
	}	
	
	public ArrayList<String> toXML(String treatAs) {
		return toXML(treatAs, 0);
	}
}
