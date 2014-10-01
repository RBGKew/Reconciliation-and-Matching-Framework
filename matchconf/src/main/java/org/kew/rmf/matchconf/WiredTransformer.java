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

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * A WiredTransformer complicates the whole thing a lot unfortunately; it has
 * to exist as it adds an order (by a numeric 'rank') to the {@link Transformer}
 * it wires to a column.
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findWiredTransformersByTransformer" })
public class WiredTransformer implements Comparable<WiredTransformer> {

    private int rank;

    @ManyToOne
    private Transformer transformer;

    @Override
    public int compareTo(WiredTransformer o) {
        return this.rank - o.rank;
    }

    public WiredTransformer cloneMe(Configuration configClone) throws Exception {
        WiredTransformer clone = new WiredTransformer();
        clone.setRank(this.rank);
        clone.setTransformer(this.getTransformer().cloneMe(configClone));
        return clone;
    }

    public String getName() {
        return this.getRank() + "_" + this.getTransformer().getName();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.getRank(), this.getTransformer().getName());
    }

    /**
     * Helper method to deal with the problem of orphaned WiredTransformers;
     * Once this problem is sorted out properly please delete!
     * @return
     */
    public boolean isWireOrphan() {
        for (Wire wire:this.transformer.getConfiguration().getWiring()) {
            if (wire.getQueryTransformerForName(this.getName()) != null) return false;
            if (wire.getAuthorityTransformerForName(this.getName()) != null) return false;
        }
        return true;
    }
}
