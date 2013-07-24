package org.kew.shs.dedupl.matchconf;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Reporter {

    private String name;

    private String delimiter = "&#09;";
    private String idDelimiter = "|";
    private String fileName;

    private String packageName = "org.kew.shs.dedupl.reporters";
    private String className = "LuceneReporter";

    private String params;

    @ManyToOne
    private Configuration config;


}
