package org.kew.shs.dedupl.matchconf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.kew.shs.dedupl.matchconf.utils.GetterSetter;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@MappedSuperclass
@RooJpaActiveRecord(mappedSuperclass=true)
public abstract class CloneMe<T> {

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

}
