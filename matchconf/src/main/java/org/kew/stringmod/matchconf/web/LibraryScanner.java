package org.kew.stringmod.matchconf.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kew.stringmod.utils.LibraryRegister;
import org.reflections.Reflections;

/**
 * The LibraryScanner filters for all LibraryRegister-annotated classes in the
 * provided `BASE_PACKAGES`.
 *
 * It produces a map that groups the classes in packages.
 */
public class LibraryScanner {

	public static final String[] BASE_PACKAGES = {
		"org.kew.stringmod.dedupl.*", // the core deduplicator
		"org.kew.stringmod.lib.*"     // the lib containing re-usable transformers etc.
	};

    /**
     * Produces a map that groups the classes in packages.
     */
    public static Map<String, Map<String, List<String>>> availableItems() {
        Map<String, Map<String, List<String>>> items = new HashMap<>();
        Set<Class<?>> clazzes = new HashSet<>();
        for (String packName : BASE_PACKAGES) {
            Reflections reflections = new Reflections(packName);
            clazzes.addAll(reflections.getTypesAnnotatedWith(LibraryRegister.class));
        }
        // TODO: add a field to Configuration that enables a user to add any package on the
        //        classpath to the available items
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
        return StringUtils.join(myList, ".");
    }

}
