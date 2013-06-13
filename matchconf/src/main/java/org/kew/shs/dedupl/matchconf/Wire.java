package org.kew.shs.dedupl.matchconf;
import javax.persistence.ManyToOne;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Size;

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
    private Configuration configuration;

    @ManyToOne
    private Bot matcher;

    @Size(max = 1)
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Bot> transformer = new HashSet<Bot>();

    @Override
    public int compareTo(Wire w) {
        return this.columnIndex - w.columnIndex;
    }

}
