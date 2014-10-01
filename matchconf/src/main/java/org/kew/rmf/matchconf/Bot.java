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
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * A Bot serves as a template for all configurable {@link Matcher} and {@link Transformer}
 * entities that have a (instance) name,
 * a package- and a className for the class to be identified and accept an arbitrary
 * amount of comma-separated 'param=value' parameters.
 */
@RooJavaBean
@RooToString
@MappedSuperclass
@RooJpaActiveRecord(mappedSuperclass=true)
public abstract class Bot extends CloneMe<Bot> implements Comparable<Bot> {

	static String[] CLONE_STRING_FIELDS = new String[] {
        "name",
        "packageName",
        "className",
		"params",
	};

    @Override
    public int compareTo(Bot o) {
        return this.getName().compareTo(o.getName());
    }

    public abstract String getName();
    public abstract String getPackageName();
    public abstract String getClassName();
    public abstract String getParams();

    public abstract List<? extends Bot> getComposedBy();

    public abstract String getGroup();

    @Override
    public String toString () {
        return this.getName();
    }
}
