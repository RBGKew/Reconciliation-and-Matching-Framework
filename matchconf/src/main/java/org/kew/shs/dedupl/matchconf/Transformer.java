package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord()
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"configuration", "name"}))
public class Transformer extends Bot {

    private String name;
    private String packageName;
    private String className;
    private String params;

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

    public void removeWiredTransformers() throws Exception {
        try {
            this.removeWiredTransformersLongWay();
        } catch (Exception e) {
            if (e instanceof Exception) {
                throw new Exception(String.format("It seems that %s is still being used in a wire, please remove it there first in order to delete it.", this));
            }
        }
    }

    public Wire hasWiredTransformers() {
        for (Wire wire:this.getConfiguration().getWiring()) {
            for (WiredTransformer wt:wire.getSourceTransformers()) {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    return wire;
                }
            }
            for (WiredTransformer wt:wire.getLookupTransformers()) {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    return wire;
                }
            }
        }
        return null;
    }

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

    public void removeWiredTransformersLongWay() {
        List<WiredTransformer> wts;
        for (Wire wire:this.getConfiguration().getWiring()) {
            wts = wire.getSourceTransformers();
            for (WiredTransformer wt:new ArrayList<WiredTransformer>(wts)) {
                if (wt.getTransformer().getName().equals(this.getName())) {
                    wts.remove(wt);
                }
            }
            wts = wire.getLookupTransformers();
            for (WiredTransformer wt:new ArrayList<WiredTransformer>(wts)) {
                if (wt.getTransformer().getName().equals(this.getName())) wts.remove(wt);
            }
            wire.merge();
        }
    }

}
