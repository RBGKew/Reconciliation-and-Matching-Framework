package org.kew.shs.dedupl.matchconf.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kew.shs.dedupl.util.LibraryRegister;
import org.reflections.Reflections;

public class LibraryScanner {

    public static Map<String, Map<String, List<String>>> availableItems() {
        Map<String, Map<String, List<String>>> items = new HashMap<>();
        Reflections reflections = new Reflections("org.kew.shs.dedupl.*");
        Set<Class<?>> clazzes = reflections.getTypesAnnotatedWith(LibraryRegister.class);
        for (Class<?> clazz:clazzes) {
            LibraryRegister annotation = (LibraryRegister) clazz.getAnnotation(LibraryRegister.class);
            String category = annotation.category();
            if (items.get(category) == null) items.put(category,  new HashMap<String, List<String>>());
            String packageName = getPackageName(clazz.getName());
            if (items.get(category).get(packageName) == null) items.get(category).put(packageName, new ArrayList<String>());
            items.get(category).get(packageName).add(clazz.getSimpleName());
        }
        for (Map<String, List<String>> itemMap:items.values()) {
            for (List<String> itemList:itemMap.values()) {
                Collections.sort(itemList);
            }

        }
        return items;
    }

    public static String getPackageName(String packageAndClass) {
        String[] myList = packageAndClass.split("\\.");
        myList = Arrays.copyOfRange(myList, 0, myList.length - 1);
        // crop the 'irrelevant' first bits of the package name to one character only
        for (int i=0;i<myList.length - 2;i++) {
            myList[i] = myList[i].substring(0,1);
        }
        return StringUtils.join(myList, ".");
    }

}
