package org.kew.stringmod.matchconf.web;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.kew.rmf.transformers.Transformer;
import org.kew.stringmod.dedupl.matchers.Matcher;
import org.kew.stringmod.dedupl.reporters.Reporter;
import org.reflections.Reflections;

/**
 * The LibraryScanner finds Transformers, Matchers and Reporters and produces a map that groups the classes in packages.
 */
public class LibraryScanner {
	/**
	 * Produces a map that groups the classes in packages.
	 */
	public static Map<String, Map<String, Set<String>>> availableItems() {
		Map<String, Map<String, Set<String>>> items = new TreeMap<>();

		items.put("matchers", classesImplementingInterface(Matcher.class));
		items.put("transformers", classesImplementingInterface(Transformer.class));
		items.put("reporters", classesImplementingInterface(Reporter.class));

		return items;
	}

	private static Map<String, Set<String>> classesImplementingInterface(Class<?> ınterface) {
		Map<String, Set<String>> classesByPackage = new TreeMap<>();

		Reflections reflections = new Reflections(ınterface.getPackage().getName());

		for (Class<?> clazz : reflections.getSubTypesOf(ınterface)) {
			if (Modifier.isAbstract(clazz.getModifiers())) continue;
			String packageName = clazz.getPackage().getName();
			if (classesByPackage.get(packageName) == null) classesByPackage.put(packageName, new TreeSet<String>());
			classesByPackage.get(packageName).add(clazz.getSimpleName());
		}

		return classesByPackage;
	}
}
