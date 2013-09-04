package org.kew.shs.dedupl.matchconf;
import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findWiredTransformersByTransformer" })
public class WiredTransformer implements Comparable<WiredTransformer> {

    private int rank;

    @ManyToOne
    private Transformer transformer;

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

    public String toString() {
        return String.format("%s: %s", this.getRank(), this.getTransformer().getName());
    }
}
