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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.kew.rmf.matchconf.utils.GetterSetter;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * This is the ORM equivalent to any implementation of
 * {@link org.kew.rmf.core.configuration.Property}.
 *
 * It maps named columns to a {@link Matcher} per column and 0:n {@link Transformer}s per column.
 * It also takes care of dealing with the separation of query- and authority- {@link Transformer}s.
 *
 * The link to the Transformers is via {@link WiredTransformer}s.
 */
@RooJavaBean
@RooToString
@Table(uniqueConstraints = @javax.persistence.UniqueConstraint(columnNames = { "configuration", "queryColumnName", "authorityColumnName" }))
@RooJpaActiveRecord(finders = { "findWiresByMatcher" })
public class Wire extends CloneMe<Wire> implements Comparable<Wire> {

    static String[] CLONE_STRING_FIELDS = new String[] { "authorityColumnName", "queryColumnName" };

    static String[] CLONE_BOOL_FIELDS = new String[] { "addOriginalQueryValue", "addOriginalAuthorityValue", "addTransformedAuthorityValue", "addTransformedQueryValue", "blanksMatch", "indexInitial", "indexLength", "useInNegativeSelect", "useInSelect", "useWildcard" };

    private String queryColumnName;

    private String authorityColumnName = "";

    public Boolean useInSelect = false;

    public Boolean useInNegativeSelect = false;

    public Boolean indexLength = false;

    public Boolean blanksMatch = false;

    public Boolean addOriginalQueryValue = false;

    public Boolean addOriginalAuthorityValue = false;

    public Boolean addTransformedQueryValue = false;

    public Boolean addTransformedAuthorityValue = false;

    public Boolean indexInitial = false;

    public Boolean useWildcard = false;

    @ManyToOne
    private Matcher matcher;

    @ManyToOne
    private Configuration configuration;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    private List<WiredTransformer> queryTransformers = new ArrayList<WiredTransformer>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WiredTransformer> authorityTransformers = new ArrayList<WiredTransformer>();

    public String getName() {
        return this.getQueryColumnName() + "_" + this.getAuthorityColumnName();
    }

    @Override
    public int compareTo(Wire w) {
        return this.getName().compareTo(w.getName());
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public Wire cloneMe(Configuration configClone) throws Exception {
        Wire clone = new Wire();
        // first the string attributes
        for (String method : Wire.CLONE_STRING_FIELDS) {
            clone.setattr(method, this.getattr(method, ""));
        }
        for (String method : Wire.CLONE_BOOL_FIELDS) {
            clone.setattr(method, this.getattr(method, true));
        }
        // then the relational attributes
        clone.setConfiguration(configClone);
        clone.setMatcher(this.matcher.cloneMe(configClone));
        for (WiredTransformer trans : this.getQueryTransformers()) {
            clone.getQueryTransformers().add(trans.cloneMe(configClone));
        }
        for (WiredTransformer trans : this.getAuthorityTransformers()) {
            clone.getAuthorityTransformers().add(trans.cloneMe(configClone));
        }
        return clone;
    }

    public WiredTransformer getWiredTransformer(String transformerType, String transformerName) throws Exception {
        for (WiredTransformer wT : new GetterSetter<List<WiredTransformer>>().getattr(this, transformerType + "Transformers")) {
            if (wT.getName().equals(transformerName)) return wT;
        }
        ;
        return null;
    }

    public WiredTransformer getQueryTransformerForName(String wiredTransformerName) {
        for (WiredTransformer wiredTransformer : this.getQueryTransformers()) {
            if (wiredTransformer.getName().equals(wiredTransformerName)) return wiredTransformer;
        }
        return null;
    }

    public WiredTransformer getAuthorityTransformerForName(String wiredTransformerName) {
        for (WiredTransformer wiredTransformer : this.getAuthorityTransformers()) {
            if (wiredTransformer.getName().equals(wiredTransformerName)) return wiredTransformer;
        }
        return null;
    }
}
