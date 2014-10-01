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
package org.kew.rmf.matchconf.web;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.kew.rmf.matchers.Matcher;
import org.kew.rmf.reporters.Reporter;
import org.kew.rmf.transformers.Transformer;
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
