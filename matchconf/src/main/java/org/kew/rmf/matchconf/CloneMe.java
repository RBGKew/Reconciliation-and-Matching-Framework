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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostRemove;
import javax.persistence.PreRemove;

import org.kew.rmf.matchconf.utils.GetterSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * Think of this abstract class as if it provided an abstract method 'cloneMe', I didn't get it
 * working explicitely due to some conflicts between generics, raw types and the way the ORM is
 * built, but this is how it's used.
 *
 * @param <T>
 */
@RooJavaBean
@RooToString
@MappedSuperclass
@RooJpaActiveRecord(mappedSuperclass=true)
public abstract class CloneMe<T> {

    protected static Logger logger = LoggerFactory.getLogger(CloneMe.class);

    public int getattr(String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return new GetterSetter<Integer>().getattr(this, fieldName);
    }
    public Boolean getattr(String fieldName, Boolean b) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return new GetterSetter<Boolean>().getattr(this, fieldName);
    }
    public String getattr(String fieldName, String s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return new GetterSetter<String>().getattr(this, fieldName);
    }
    public void setattr(String fieldName, int n) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        new GetterSetter<Integer>().setattr(this, fieldName, n);;
    }
    public void setattr(String fieldName, Boolean b) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        new GetterSetter<Boolean>().setattr(this, fieldName, b);
    }
    public void setattr(String fieldName, String s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        if (s!= null) new GetterSetter<String>().setattr(this, fieldName, s);
    }

    // conflict of Generics, inheritance and spring roo as far as I understand; have moved this method out of Roo's way,
    // not using it anyway
    public static List<CloneMe<Object>> findAllCloneMes() {
        return new ArrayList<CloneMe<Object>>();
    }
    // conflict of Generics, inheritance and spring roo as far as I understand; have moved this method out of Roo's way,
    // not using it anyway
     public static List<CloneMe<Object>> findCloneMeEntries(int firstResult, int maxResults) {
        return new ArrayList<CloneMe<Object>>();
    }

    @PreRemove
    public void loggPreRemoval() {
        logger.warn("Attempt to delete {} with id {}", this, this.getId());
    }

    @PostRemove
    public void loggRemoval() {
        logger.info("Deleted {}", this);
    }

}
