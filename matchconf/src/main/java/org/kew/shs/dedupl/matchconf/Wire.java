package org.kew.shs.dedupl.matchconf;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"configuration", "sourceColumnName", "lookupColumnName"}))
public class Wire extends CloneMe<Wire> implements Comparable<Wire> {

    static String[] CLONE_STRING_FIELDS = new String[] {
        "lookupColumnName",
        "sourceColumnName",
    };
    static String[] CLONE_BOOL_FIELDS = new String[] {
        "addOriginalSourceValue",
        "addOriginalLookupValue",
        "addTransformedLookupValue",
        "addTransformedSourceValue",
        "blanksMatch",
        "indexInitial",
        "indexLength",
        "useInNegativeSelect",
        "useInSelect",
        "useWildcard",
    };

    private String sourceColumnName;
    private String lookupColumnName = "";

    public Boolean useInSelect = false;
    public Boolean useInNegativeSelect = false;
    public Boolean indexLength = false;
    public Boolean blanksMatch = false;
    public Boolean addOriginalSourceValue = false;
    public Boolean addOriginalLookupValue = false;
    public Boolean addTransformedSourceValue = false;
    public Boolean addTransformedLookupValue = false;
    public Boolean indexInitial = false;
    public Boolean useWildcard = false;

    @ManyToOne
    private Matcher matcher;

    @ManyToOne
    private Configuration configuration;

    @ManyToMany
    @Size(min=0)
    @Sort(type=SortType.NATURAL)
    private List<Transformer> sourceTransformers = new ArrayList<Transformer>();

    @ManyToMany
    @Size(min=0)
    private List<Transformer> lookupTransformers = new ArrayList<Transformer>();

    public String getName() {
        return this.getSourceColumnName() + "_" + this.getLookupColumnName();
    }
    @Override
    public int compareTo(Wire w) {
        return this.getName().compareTo(w.getName());
    }

    public String toString() {
        return this.getName();
    }

    public Wire cloneMe(Configuration configClone) throws Exception {
        Wire clone = new Wire();
        // first the string attributes
        for (String method:Wire.CLONE_STRING_FIELDS) {
            clone.setattr(method, this.getattr(method, ""));
        }
        for (String method:Wire.CLONE_BOOL_FIELDS) {
            clone.setattr(method, this.getattr(method, true));
        }
        // then the relational attributes
        clone.setConfiguration(configClone);
        clone.setMatcher(this.matcher.cloneMe(configClone));
        for (Transformer trans:this.getSourceTransformers()) {
            clone.getSourceTransformers().add(trans.cloneMe(configClone));
        }
        for (Transformer trans:this.getLookupTransformers()) {
            clone.getLookupTransformers().add(trans.cloneMe(configClone));
        }
        clone.persist();
        return clone;
    }
}
