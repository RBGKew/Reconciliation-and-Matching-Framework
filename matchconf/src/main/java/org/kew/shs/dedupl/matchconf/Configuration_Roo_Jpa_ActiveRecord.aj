// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.shs.dedupl.matchconf;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.kew.shs.dedupl.matchconf.Configuration;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Configuration_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Configuration.entityManager;
    
    public static final EntityManager Configuration.entityManager() {
        EntityManager em = new Configuration().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Configuration.countConfigurations() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Configuration o", Long.class).getSingleResult();
    }
    
    public static List<Configuration> Configuration.findAllConfigurations() {
        return entityManager().createQuery("SELECT o FROM Configuration o", Configuration.class).getResultList();
    }
    
    public static Configuration Configuration.findConfiguration(Long id) {
        if (id == null) return null;
        return entityManager().find(Configuration.class, id);
    }
    
    public static List<Configuration> Configuration.findConfigurationEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Configuration o", Configuration.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Configuration.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Configuration.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Configuration attached = Configuration.findConfiguration(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Configuration.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Configuration.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Configuration Configuration.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Configuration merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
