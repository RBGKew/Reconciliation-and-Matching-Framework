package org.kew.rmf.matchconf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


/**
 * This is the ORM equivalent to any implementation of
 * {@link org.kew.rmf.matchers.Matcher}.
 *
 * It can describe any matcher, the provided params are expected to be a comma-separated
 * String of key=value pairs.
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"configuration", "name"}))
public class Matcher extends Bot {

    private String name;
    private String packageName;
    private String className;
    private String params;

    @Transient
    private final String group = "matchers";

    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Matcher> composedBy = new ArrayList<Matcher>();

    @ManyToOne
    private Configuration configuration;

    public String toString () {
        return this.getName();
    }

    public Matcher cloneMe(Configuration configClone) throws Exception {
        Matcher alreadyCloned = configClone.getMatcherForName(this.name);
        if (alreadyCloned != null) return alreadyCloned;
        Matcher clone = new Matcher();
        // first the string attributes
        for (String method:Bot.CLONE_STRING_FIELDS) {
            clone.setattr(method, this.getattr(method, ""));
        }
        // then the relational attributes
        clone.setConfiguration(configClone);
        for (Matcher component:this.composedBy) {
            Matcher compoClone = component.cloneMe(configClone);
            clone.getComposedBy().add(compoClone);
        }
        return clone;
    }

    @Override
	public String getGroup() {
		return group;
	}
}
