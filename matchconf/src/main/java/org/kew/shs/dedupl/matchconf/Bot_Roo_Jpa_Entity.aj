// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.shs.dedupl.matchconf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import org.kew.shs.dedupl.matchconf.Bot;

privileged aspect Bot_Roo_Jpa_Entity {
    
    declare @type: Bot: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Bot.id;
    
    @Version
    @Column(name = "version")
    private Integer Bot.version;
    
    public Long Bot.getId() {
        return this.id;
    }
    
    public void Bot.setId(Long id) {
        this.id = id;
    }
    
    public Integer Bot.getVersion() {
        return this.version;
    }
    
    public void Bot.setVersion(Integer version) {
        this.version = version;
    }
    
}
