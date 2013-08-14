package org.kew.shs.dedupl.matchconf;

import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"config", "name"}))
public class Dictionary {

    String name;
    String fileDelimiter = "&#09;";

    @NotNull
    String fileName;

    @ManyToOne
    Configuration config;

    public Dictionary cloneMe () {
        Dictionary clone = new Dictionary();
        clone.setName(this.getName());
        clone.setFileDelimiter(this.getFileDelimiter());
        clone.setFileName(this.fileName);
        clone.setConfig(this.getConfig());
        return clone;
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }
    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



}
