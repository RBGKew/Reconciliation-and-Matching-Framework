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
    private List<Transformer> composedBy = new ArrayList<Transformer>();

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
        clone.persist();
        return clone;
    }
}
