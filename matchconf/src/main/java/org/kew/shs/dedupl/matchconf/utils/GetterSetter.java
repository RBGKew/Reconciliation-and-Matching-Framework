package org.kew.shs.dedupl.matchconf.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;

public class GetterSetter<ReturnType> {

    @SuppressWarnings("unchecked")
    public <ObjClass> ReturnType getattr(ObjClass obj, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "get" + StringUtils.capitalise(fieldName);
        return (ReturnType) obj.getClass().getMethod(method, null).invoke(obj, null);
    }

    public <T> void setattr(T obj, String fieldName, int n) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalise(fieldName);
        obj.getClass().getMethod(method, int.class).invoke(obj, n);
    }
    public <T> void setattr(T obj, String fieldName, ReturnType b) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalise(fieldName);
        obj.getClass().getMethod(method, b.getClass()).invoke(obj, b);
    }

}
