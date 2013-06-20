package org.kew.shs.dedupl.matchconf;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Wire implements Comparable<Wire> {

    private String columnName;

    private Integer columnIndex;

    private Boolean useInSelect = false;

    private Boolean useInNegativeSelect = false;

    private Boolean indexLength = false;

    private Boolean blanksMatch = false;

    private Boolean indexOriginal = false;

    private Boolean indexInitial = false;

    private Boolean useWildcard = false;

    @ManyToOne
    private Matcher matcher;

    @Size(max = 1)
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Transformer> transformer = new HashSet<Transformer>();

    @Override
    public int compareTo(Wire w) {
        return this.columnIndex - w.columnIndex;
    }

}
