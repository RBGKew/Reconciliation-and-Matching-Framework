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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * This is the ORM equivalent to any implementation of
 * {@link org.kew.rmf.transformers.Transformer}.
 *
 * It can describe any matcher, the provided params are expected to be a comma-separated
 * String of key=value pairs.
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord()
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"configuration", "name"}))
public class Transformer extends Bot {

    private String name;
    private String packageName;
    private String className;
    private String params;

    @Transient
    private final String group = "transformers";

    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Transformer> composedBy = new ArrayList<>();

    @ManyToOne
    private Configuration configuration;

    public String toString () {
        return this.getName();
    }

    public Transformer cloneMe(Configuration configClone) throws Exception {
        Transformer alreadyCloned = configClone.getTransformerForName(this.name);
        if (alreadyCloned != null) return alreadyCloned;
        Transformer clone = new Transformer();
        // first the string attributes
        for (String method:Bot.CLONE_STRING_FIELDS) {
            clone.setattr(method, this.getattr(method, ""));
        }
        // then the relational attributes
        clone.setConfiguration(configClone);
        for (Transformer component:this.composedBy) {
            Transformer compoClone = component.cloneMe(configClone);
            clone.getComposedBy().add(compoClone);
        }
        return clone;
    }

    /**
     * Helper method to deal with the problem of orphaned WiredTransformer instances.
     * If this problem is sorted please delete!
     *
     * @throws Exception
     */
    public void removeWiredTransformers() throws Exception {
        try {
            this.removeWiredTransformersLongWay();
        } catch (Exception e) {
            if (e instanceof Exception) {
                throw new Exception(String.format("It seems that %s is still being used in a wire, please remove it there first in order to delete it.", this));
            }
        }
    }

    /**
     * Helper method to deal with the problem of orphaned WiredTransformer instances.
     * If this problem is sorted please delete!
     */
    public Wire hasWiredTransformers() {
        for (Wire wire:this.getConfiguration().getWiring()) {
            for (WiredTransformer wt:wire.getQueryTransformers()) {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    return wire;
                }
            }
            for (WiredTransformer wt:wire.getAuthorityTransformers()) {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    return wire;
                }
            }
        }
        return null;
    }

    /**
     * Helper method to deal with the problem of orphaned WiredTransformer instances.
     * If this problem is sorted please delete!
     */
    public void removeOrphanedWiredTransformers() {
        List<WiredTransformer> all_wts = WiredTransformer.findAllWiredTransformers();
        for (WiredTransformer wt:all_wts) {
            try {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    if (wt.isWireOrphan()) {
                        logger.warn("Still found orphaned WiredTransformer with id {}!!! trying to remove..", wt.getId());
                        wt.remove();
                        logger.info("Released orphaned WiredTransformer into, er, liberty.");
                    }
                }
            } catch (Exception e) {
                if (wt.getTransformer() == null) {
                    logger.warn("Still found orphaned WiredTransformer with id {}!!! trying to remove..", wt.getId());
                    wt.remove();
                    logger.info("Released orphaned WiredTransformer into, er, liberty.");
                } else throw e;
            }
        }
    }

    /**
     * Helper method to deal with the problem of orphaned WiredTransformer instances.
     * If this problem is sorted please delete!
     */
    public void removeWiredTransformersLongWay() {
        List<WiredTransformer> wts;
        for (Wire wire:this.getConfiguration().getWiring()) {
            wts = wire.getQueryTransformers();
            for (WiredTransformer wt:new ArrayList<WiredTransformer>(wts)) {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    wts.remove(wt);
                }
            }
            wts = wire.getAuthorityTransformers();
            for (WiredTransformer wt:new ArrayList<WiredTransformer>(wts)) {
                if (wt.getTransformer().getName().equals(this.getName())) wts.remove(wt);
            }
            wire.merge();
        }
    }

    @Override
	public String getGroup() {
		return group;
	}

}
