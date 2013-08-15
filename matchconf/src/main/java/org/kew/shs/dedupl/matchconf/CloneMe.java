package org.kew.shs.dedupl.matchconf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@MappedSuperclass
@RooJpaActiveRecord(mappedSuperclass=true)
public abstract class CloneMe<T> {

    public int getattr(String fieldName, int n) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "get" + StringUtils.capitalise(fieldName);
        return (int) this.getClass().getMethod(method, null).invoke(this, null);
    }
    public Boolean getattr(String fieldName, Boolean b) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "get" + StringUtils.capitalise(fieldName);
        return (Boolean) this.getClass().getMethod(method, null).invoke(this, null);
    }
    public String getattr(String fieldName, String s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "get" + StringUtils.capitalise(fieldName);
        return (String) this.getClass().getMethod(method, null).invoke(this, null);
    }

    public void setattr(String fieldName, int n) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalise(fieldName);
        this.getClass().getMethod(method, int.class).invoke(this, n);
    }
    public void setattr(String fieldName, Boolean b) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalise(fieldName);
        this.getClass().getMethod(method, Boolean.class).invoke(this, b);
    }
    public void setattr(String fieldName, String s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalise(fieldName);
        this.getClass().getMethod(method, String.class).invoke(this, s);
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

}
