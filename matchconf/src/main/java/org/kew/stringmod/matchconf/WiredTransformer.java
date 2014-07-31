package org.kew.stringmod.matchconf;
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
