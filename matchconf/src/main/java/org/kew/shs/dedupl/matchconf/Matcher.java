package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Matcher extends Bot {

    private String name;
    private String packageName;
    private String className;
    private String params;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "matcher")
    private Set<Wire> matchedWires = new HashSet<Wire>();

    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Matcher> composedBy = new ArrayList<Matcher>();

}
