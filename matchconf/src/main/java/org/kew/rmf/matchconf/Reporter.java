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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 *
 * This is the ORM equivalent to any heir of {@link org.kew.rmf.reporters.Reporter}.
 *
 * It can describe any reporter, the provided params are expected to be a comma-separated
 * String of key=value pairs.
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"config", "name"}))
public class Reporter extends CloneMe<Reporter> {

    static String[] CLONE_STRING_FIELDS = new String[] {
        "className",
        "delimiter",
        "fileName",
        "idDelimiter",
        "name",
        "packageName",
        "params",
    };

    private String name;

    private String delimiter = "&#09;";
    private String idDelimiter = "|";
    private String fileName;

    private String packageName;
    private String className;

    private String params;

    @ManyToOne
    private Configuration config;

    public String toString() {
        return this.name;
    }

    public Reporter cloneMe(Configuration configClone) throws Exception {
        Reporter clone = new Reporter();
        // first the string attributes and manytoones
        // first the string attributes
        for (String method:Reporter.CLONE_STRING_FIELDS) {
            clone.setattr(method, this.getattr(method, ""));
        }
        // then the relational attributes
        clone.setConfig(configClone);
        return clone;
    }

}
