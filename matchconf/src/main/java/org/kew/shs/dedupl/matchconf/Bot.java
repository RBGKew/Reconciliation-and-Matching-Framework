package org.kew.shs.dedupl.matchconf;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@MappedSuperclass
@RooJpaActiveRecord(mappedSuperclass=true)
public abstract class Bot implements Comparable<Bot> {

    private String name;
    private String packageName;
    private String className;
    private String params;

    public int compareTo(Bot o) {
        return this.name.compareTo(o.name);
    }

    public abstract List<? extends Bot> getComposedBy();

}
