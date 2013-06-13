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
public class Bot implements Comparable<Bot> {

    private String name;

    private String packageName;

    private String className;

    private String params;

    //    @ManyToOne(optional=true)
    //   private Bot composer = null;
    //
    //   @OneToMany(cascade = CascadeType.ALL, mappedBy = "composer")
    //  @Sort(type=SortType.NATURAL)
    // private SortedSet<Bot> composedBy = new TreeSet<Bot>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "matcher")
    private Set<Wire> matchedWires = new HashSet<Wire>();

    @Override
    public int compareTo(Bot o) {
        return this.name.compareTo(o.name);
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Bot> composedBy = new ArrayList<Bot>();

}
