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
public abstract class Bot implements Comparable<Bot>, Cloneable {

    public int compareTo(Bot o) {
        return this.getName().compareTo(o.getName());
    }

    public abstract String getName();
    public abstract String getPackageName();
    public abstract String getClassName();
    public abstract String getParams();

    public abstract List<? extends Bot> getComposedBy();

    public String toString () {
        return this.getName();
    }

}
