package org.kew.rmf.matchconf;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 *
 * This is the ORM equivalent to any heir of {@link org.kew.rmf.reporters.Reporter}.
 *
 * It can describe any reporter, the provided params are expected to be a comma-separated
 * String of key=value pairs.
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"config", "name"}))
public class Reporter extends CloneMe<Reporter> {

    static String[] CLONE_STRING_FIELDS = new String[] {
        "className",
        "delimiter",
        "fileName",
        "idDelimiter",
        "name",
        "packageName",
        "params",
    };

    private String name;

    private String delimiter = "&#09;";
    private String idDelimiter = "|";
    private String fileName;

    private String packageName;
    private String className;

    private String params;

    @ManyToOne
    private Configuration config;

    public String toString() {
        return this.name;
    }

    public Reporter cloneMe(Configuration configClone) throws Exception {
        Reporter clone = new Reporter();
        // first the string attributes and manytoones
        // first the string attributes
        for (String method:Reporter.CLONE_STRING_FIELDS) {
            clone.setattr(method, this.getattr(method, ""));
        }
        // then the relational attributes
        clone.setConfig(configClone);
        return clone;
    }

}
