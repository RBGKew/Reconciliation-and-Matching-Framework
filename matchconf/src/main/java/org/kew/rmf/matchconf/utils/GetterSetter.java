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
package org.kew.rmf.matchconf.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;

/**
 * A helper class to get and set attributes via conventional getField and setField methods
 * using generics.
 *
 * @param <ReturnType>
 */
public class GetterSetter<ReturnType> {

    @SuppressWarnings("unchecked")
    public <ObjClass> ReturnType getattr(ObjClass obj, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "get" + StringUtils.capitalize(fieldName);
        return (ReturnType) obj.getClass().getMethod(method).invoke(obj);
    }

    public <T> void setattr(T obj, String fieldName, int n) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalize(fieldName);
        obj.getClass().getMethod(method, int.class).invoke(obj, n);
    }
    public <T> void setattr(T obj, String fieldName, ReturnType b) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "set" + StringUtils.capitalize(fieldName);
        obj.getClass().getMethod(method, b.getClass()).invoke(obj, b);
    }

}
