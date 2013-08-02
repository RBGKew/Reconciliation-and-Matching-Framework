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
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"configuration", "name"}))
public class Matcher extends Bot {

    private String name;
    private String packageName;
    private String className;
    private String params;

    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Matcher> composedBy = new ArrayList<Matcher>();

    @ManyToOne
    private Configuration configuration;

    public String toString () {
        return this.getName();
    }

    public Matcher clone(Configuration config) {
        Matcher matcher = config.getMatcherForName(this.name);
        if (matcher != null) return matcher;
        matcher = new Matcher();
        // first the string attributes and manytoones
        matcher.setName(this.name);
        matcher.setPackageName(this.packageName);
        matcher.setClassName(this.className);
        matcher.setParams(this.params);
        matcher.setConfiguration(config);
        matcher.persist();
        // then the relational attributes
        for (Matcher component:this.composedBy) {
            component.clone(config);
            matcher.getComposedBy().add(component);
        }
        matcher.merge();
        return matcher;
    }

}
