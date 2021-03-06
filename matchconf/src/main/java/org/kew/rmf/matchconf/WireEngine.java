/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.matchconf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Writes out the xml to configure the 'real' deduplication/matching process of this specific
 * {@link Wire} instance.
 */
public class WireEngine {

    Wire wire;

    private final String[] defaultFalseList = new String[] {
            "useInSelect", "useInNegativeSelect", "indexLength", "blanksMatch",
            "addOriginalQueryValue", "addOriginalAuthorityValue",
            "addTransformedQueryValue", "addTransformedAuthorityValue", "indexInitial",
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
        outXML.add(String.format("%s<bean class=\"org.kew.rmf.core.configuration.Property\"", indent));
        outXML.add(String.format("%s%sp:queryColumnName=\"%s\"", indent, shift, this.wire.getQueryColumnName()));
        if (this.wire.getAuthorityColumnName().length() > 0) {
	        outXML.add(String.format("%s%sp:authorityColumnName=\"%s\"", indent, shift, this.wire.getAuthorityColumnName()));
        }

        // all boolean attributes default to false and we only want to write them if they are set to true
        for (String attr:this.defaultFalseList) {
            boolean value = (boolean) this.wire.getClass().getField(attr).get(this.wire);
            if (value) outXML.add(String.format("%s%sp:%s=\"%s\"", indent, shift, attr, value));
        }

        outXML.add(String.format("%s%sp:matcher-ref=\"%s\">", indent, shift, this.wire.getMatcher().getName()));

        List<WiredTransformer> queryTransens = this.wire.getQueryTransformers();
        if (queryTransens.size() > 0) {
            outXML.add(String.format("%s%s<property name=\"queryTransformers\">", indent, shift));
            outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
            Collections.sort(queryTransens);
            for (WiredTransformer wTrans:queryTransens) {
                outXML.add(String.format("%s%s%s%s<ref bean=\"%s\"/>", indent, shift,shift,  shift, wTrans.getTransformer().getName()));
            }
            outXML.add(String.format("%s%s%s</util:list>", indent, shift, shift));
            outXML.add(String.format("%s%s</property>", indent, shift));
        }
        List<WiredTransformer> authorityTransens = this.wire.getAuthorityTransformers();
        if (authorityTransens.size() > 0) {
            outXML.add(String.format("%s%s<property name=\"authorityTransformers\">", indent, shift));
            outXML.add(String.format("%s%s%s<util:list id=\"1\">", indent, shift, shift));
            Collections.sort(authorityTransens);
            for (WiredTransformer wTrans:authorityTransens) {
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
