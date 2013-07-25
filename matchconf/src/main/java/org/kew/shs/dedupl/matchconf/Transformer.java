package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Transformer clone(Configuration config) {
        Transformer transformer = config.getTransformerForName(this.name);
        if (transformer != null) return transformer;
        transformer = new Transformer();
        // first the string attributes and manytoones
        transformer.setName(this.name);
        transformer.setPackageName(this.packageName);
        transformer.setClassName(this.className);
        transformer.setParams(this.params);
        transformer.setConfiguration(config);
        transformer.persist();
        // then the relational attributes
        for (Transformer component:this.composedBy) {
            component.clone(config);
            transformer.getComposedBy().add(component);
        }
        transformer.merge();
        return transformer;
    }
}
