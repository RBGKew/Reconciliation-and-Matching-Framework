/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.matchconf;
import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * This is the ORM equivalent for {@link org.kew.rmf.utils.Dictionary}.
 *
 * It knows about a file that is supposed to have two columns which can be used to e.g.
 * overwrite deprecated values of columnA with the good values of columnB prior to
 * a Matching, or do falsePositives checks.
 */
@RooJavaBean
@RooToString
@Table(uniqueConstraints = @javax.persistence.UniqueConstraint(columnNames= { "name" }))
@RooJpaActiveRecord(finders = { "findDictionarysByNameEquals" })
public class Dictionary {

    String name;

    String fileDelimiter = "&#09;";

    @NotNull
    String filePath;

    @PrePersist
    @PreUpdate
    protected void cleanAllPaths() {
        // TODO: change the following local setting to a real local setting :-)
        String cleanedPath = this.getFilePath().replaceAll("\\\\", "/").replaceAll("T:", "/mnt/t_drive");
        this.setFilePath(cleanedPath);
    }

    public static TypedQuery<Dictionary> findDictionariesByNameEquals(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = Dictionary.entityManager();
        TypedQuery<Dictionary> q = em.createQuery("SELECT o FROM Dictionary AS o WHERE o.name = :name", Dictionary.class);
        q.setParameter("name", name);
        return q;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
