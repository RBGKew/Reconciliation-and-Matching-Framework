package org.kew.shs.dedupl.matchconf;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findConfigurationsByNameEquals" })
public class Configuration {

    private String name;

    private String workDirPath;

    private String inputFileName = "input.tsv";
    private String outputFileNameExtension = "tsv";

    private String packageName = "org.kew.shs.dedupl.configuration";

    private String className = "DeduplicationConfiguration";

    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Wire> wiring = new ArrayList<Wire>();

    private String inputFileEncoding = "UTF8";

    private String inputFileDelimiter = "&#09;";

    private Boolean inputFileIgnoreHeader = false;

    private String outputFileDelimiter = "&#09;";

    private String outputFileIdDelimiter = "|";

    private String loadReportFrequency = "50000";

    private String assessReportFrequency = "100";

    private String scoreFieldName = "id";

    @PrePersist
    @PreUpdate
    protected void cleanAllPaths() {
        // TODO: change the following local setting to a real local setting :-)
        String cleanedPath = this.getWorkDirPath().replaceAll("\\\\", "/").replaceAll("T:", "/mnt/t_drive");
        this.setWorkDirPath(cleanedPath);
    }
}
