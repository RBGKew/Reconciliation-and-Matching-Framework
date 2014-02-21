package org.kew.stringmod.matchconf;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * A Bot serves as a template for all configurable {@link Matcher} and {@link Transformer}
 * entities that have a (instance) name,
 * a package- and a className for the class to be identified and accept an arbitrary
 * amount of comma-separated 'param=value' parameters.
 */
@RooJavaBean
@RooToString
@MappedSuperclass
@RooJpaActiveRecord(mappedSuperclass=true)
public abstract class Bot extends CloneMe<Bot> implements Comparable<Bot> {

	static String[] CLONE_STRING_FIELDS = new String[] {
        "name",
        "packageName",
        "className",
		"params",
	};

    public int compareTo(Bot o) {
        return this.getName().compareTo(o.getName());
    }

    public abstract String getName();
    public abstract String getPackageName();
    public abstract String getClassName();
    public abstract String getParams();

    public abstract List<? extends Bot> getComposedBy();

    public abstract String getGroup();

    public String toString () {
        return this.getName();
    }

}
